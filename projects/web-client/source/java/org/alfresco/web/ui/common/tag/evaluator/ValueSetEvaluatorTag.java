/*
 * Created on 07-Apr-2005
 */
package org.alfresco.web.ui.common.tag.evaluator;

/**
 * @author kevinr
 */
public class ValueSetEvaluatorTag extends GenericEvaluatorTag
{
   /**
    * @see javax.faces.webapp.UIComponentTag#getComponentType()
    */
   public String getComponentType()
   {
      return "awc.faces.ValueSetEvaluator";
   }
}
