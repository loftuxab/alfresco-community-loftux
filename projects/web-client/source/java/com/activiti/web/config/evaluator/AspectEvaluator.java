package com.activiti.web.config.evaluator;

import jsftest.repository.DataDictionary;
import jsftest.repository.DataDictionary.MetaData;

import com.activiti.web.repository.Node;

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
    * @see com.activiti.web.config.evaluator.Evaluator#applies(java.lang.Object, java.lang.String)
    */
   public boolean applies(Object obj, String condition)
   {
      boolean result = false;
      
      if (obj instanceof Node)
      {
         String typeName = ((Node)obj).getType();
         DataDictionary dd = new DataDictionary();
         MetaData metaData = dd.getMetaData(typeName);
         result = metaData.getAspects().contains(condition);
      }
      
      return result;
   }

}
