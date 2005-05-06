/*
 * Created on 07-Apr-2005
 */
package org.alfresco.web.jsf.tag.evaluator;

/**
 * @author kevinr
 */
public class BooleanEvaluatorTag extends GenericEvaluatorTag
{
   /**
    * @see javax.faces.webapp.UIComponentTag#getComponentType()
    */
   public String getComponentType()
   {
      return "awc.faces.BooleanEvaluator";
   }
}
