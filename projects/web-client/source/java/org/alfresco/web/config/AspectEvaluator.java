package org.alfresco.web.config;

import java.util.Set;

import org.alfresco.config.evaluator.Evaluator;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.web.bean.repository.Node;

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
    * @see org.alfresco.config.evaluator.Evaluator#applies(java.lang.Object, java.lang.String)
    */
   public boolean applies(Object obj, String condition)
   {
      boolean result = false;
      
      if (obj instanceof Node)
      {
         Set aspects = ((Node)obj).getAspects();
         if (aspects != null)
         {
            // TODO: for now presume the namespace is our default one
            QName spaceQName = QName.createQName(NamespaceService.ALFRESCO_URI, condition);
            result = aspects.contains(spaceQName);
         }
      }
      
      return result;
   }

}
