package com.activiti.web.config;

import com.activiti.config.evaluator.Evaluator;
import com.activiti.web.bean.repository.Node;

/**
 * Evaluator that determines whether a given object has a particular node type
 * 
 * @author gavinc
 */
public class NodeTypeEvaluator implements Evaluator
{
   /**
    * Determines whether the given node type matches the path of the given object
    * 
    * @see com.activiti.config.evaluator.Evaluator#applies(java.lang.Object, java.lang.String)
    */
   public boolean applies(Object obj, String condition)
   {
      boolean result = false;
      
      // TODO: Also deal with NodeRef object's being passed in
      
      if (obj instanceof Node)
      {
         String type = (String)((Node)obj).getType();
         if (type != null)
         {
            result = type.equalsIgnoreCase(condition);
         }
      }
      
      return result;
   }

}
