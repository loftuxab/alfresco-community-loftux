package com.activiti.web.config;

import java.util.List;

import com.activiti.config.evaluator.Evaluator;
import com.activiti.web.bean.repository.Node;

/**
 * Evaluator that determines whether a given object has a particular aspect applied
 * 
 * @author gavinc
 */
public class AspectEvaluator implements Evaluator
{
   /**
    * Determines whether the given aspect is applied to the given object
    * 
    * @see com.activiti.config.evaluator.Evaluator#applies(java.lang.Object, java.lang.String)
    */
   public boolean applies(Object obj, String condition)
   {
      boolean result = false;
      
      // TODO: Also deal with NodeRef object's being passed in
      
      if (obj instanceof Node)
      {
         List aspects = ((Node)obj).getAspects();
         if (aspects != null)
         {
            result = aspects.contains(condition);
         }
      }
      
      return result;
   }

}
