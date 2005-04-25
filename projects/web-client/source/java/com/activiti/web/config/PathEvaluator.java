package com.activiti.web.config;

import com.activiti.config.evaluator.Evaluator;
import com.activiti.web.bean.repository.Node;

/**
 * Evaluator that determines whether a given object has a particular path
 * 
 * @author gavinc
 */
public class PathEvaluator implements Evaluator
{
   /**
    * Determines whether the given path matches the path of the given object
    * 
    * @see com.activiti.config.evaluator.Evaluator#applies(java.lang.Object, java.lang.String)
    */
   public boolean applies(Object obj, String condition)
   {
      boolean result = false;
      
      if (obj instanceof Node)
      {
         String path = (String)((Node)obj).get("path");
         if (path != null)
         {
            result = path.equalsIgnoreCase(condition);
         }
      }
      
      return result;
   }

}
