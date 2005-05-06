/*
 * Created on 07-Apr-2005
 */
package org.alfresco.web.jsf.component.evaluator;

/**
 * @author kevinr
 * 
 * Evaluates to true if the value supplied is not null.
 */
public class ValueSetEvaluator extends BaseEvaluator
{
   /**
    * Evaluate against the component attributes. Return true to allow the inner
    * components to render, false to hide them during rendering.
    * 
    * @return true to allow rendering of child components, false otherwise
    */
   public boolean evaluate()
   {
      return getValue() != null ? true : false;
   }
}
