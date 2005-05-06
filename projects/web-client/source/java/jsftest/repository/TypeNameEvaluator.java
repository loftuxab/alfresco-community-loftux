package jsftest.repository;

import org.alfresco.config.evaluator.Evaluator;
import org.alfresco.web.bean.repository.Node;

/**
 * Evaluator that tests whether an object is of a particular type
 * 
 * @author gavinc
 */
public class TypeNameEvaluator implements Evaluator
{
   /**
    * Tests whether the given object has the type defined in the condition
    * 
    * @see org.alfresco.config.evaluator.Evaluator#applies(java.lang.Object, java.lang.String)
    */
   public boolean applies(Object obj, String condition)
   {
      boolean result = false;
      
      if (obj instanceof Node)
      {
         String typeName = ((Node)obj).getType();
         result = typeName.equalsIgnoreCase(condition);
      }
      
      return result;
   }

}
