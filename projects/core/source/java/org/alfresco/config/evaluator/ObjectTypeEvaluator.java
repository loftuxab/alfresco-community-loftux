package org.alfresco.config.evaluator;

/**
 * Evaluator that tests whether an object is equal to the given class name
 * 
 * @author gavinc
 */
public class ObjectTypeEvaluator implements Evaluator
{
   /**
    * Tests whether the given object is equal to the class name given in the condition
    * 
    * @see org.alfresco.config.evaluator.Evaluator#applies(java.lang.Object, java.lang.String)
    */
   public boolean applies(Object obj, String condition)
   {
      String className = obj.getClass().getName();
      return className.equalsIgnoreCase(condition);
   }

}
