/*
 * Created on 07-Apr-2005
 */
package org.alfresco.web.jsf.component.evaluator;

import javax.faces.el.ValueBinding;

/**
 * @author kevinr
 * 
 * Evaluates to true if the value exactly matches the supplied string condition.
 */
public class StringEqualsEvaluator extends BaseEvaluator
{
   /**
    * Evaluate against the component attributes. Return true to allow the inner
    * components to render, false to hide them during rendering.
    * 
    * @return true to allow rendering of child components, false otherwise
    */
   public boolean evaluate()
   {
      boolean result = false;
      
      try
      {
         result = getCondition().equals((String)getValue());
      }
      catch (Exception err)
      {
         // return default value on error
         s_logger.debug("Expected String value for evaluation: " + getValue());
      }
      
      return result;
   }
   
   /**
    * Get the string condition to match value against
    * 
    * @return the string condition to match value against
    */
   public String getCondition()
   {
      ValueBinding vb = getValueBinding("condition");
      if (vb != null)
      {
         this.condition = (String)vb.getValue(getFacesContext());
      }
      
      return this.condition;
   }
   
   /**
    * Set the string condition to match value against
    * 
    * @param condition     string condition to match value against
    */
   public void setCondition(String condition)
   {
      this.condition = condition;
   }
   
   
   /** the string condition to match value against */
   private String condition = null;
}
