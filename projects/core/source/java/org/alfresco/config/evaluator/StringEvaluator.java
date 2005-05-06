package org.alfresco.config.evaluator;

/**
 * Evaluator that tests whether an object is equal to a string
 * 
 * @author gavinc
 */
public class StringEvaluator implements Evaluator
{
    /**
     * Tests whether the given object is equal to the string given in the condition
     * 
     * @see org.alfresco.config.evaluator.Evaluator#applies(java.lang.Object, java.lang.String)
     */
    public boolean applies(Object obj, String condition)
    {
        boolean result = false;

        if (obj instanceof String)
        {
            result = obj.toString().equals(condition);
        }

        return result;
    }

}
