package org.alfresco.config.evaluator;

/**
 * Definition of an evaluator, an object that decides whether the config section applies to
 * the current object being looked up. 
 * 
 * @author gavinc
 */
public interface Evaluator
{
   /**
    * Determines whether the given condition evaluates to true for the given object
    * 
    * @param obj The object to use as the basis for the test
    * @param condition The condition to test
    * @return true if this evaluator applies to the given object, false otherwise
    */
   public boolean applies(Object obj, String condition);
}
