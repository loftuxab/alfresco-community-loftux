package org.alfresco.web.config;

import org.alfresco.config.evaluator.Evaluator;
import org.alfresco.web.bean.repository.Node;

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
    * @see org.alfresco.config.evaluator.Evaluator#applies(java.lang.Object, java.lang.String)
    */
   public boolean applies(Object obj, String condition)
   {
      boolean result = false;
      
      // TODO: Also deal with NodeRef object's being passed in
      
      if (obj instanceof Node)
      {
         String type = (String)((Node)obj).getTypeName();
         if (type != null)
         {
            result = type.equalsIgnoreCase(condition);
         }
      }
      
      return result;
   }

}
