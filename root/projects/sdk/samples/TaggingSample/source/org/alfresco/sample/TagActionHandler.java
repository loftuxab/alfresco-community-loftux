package org.alfresco.sample;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.alfresco.web.app.Application;
import org.alfresco.web.bean.actions.handlers.BaseActionHandler;
import org.alfresco.web.bean.wizard.IWizardBean;

/**
 * Action handler for the "tag" action.
 * 
 * @author gavinc
 */
public class TagActionHandler extends BaseActionHandler
{
   public static final String PROP_TAGS = "tags";
   
   public String getJSPPath()
   {
      return "/jsp/extension/tag.jsp";
   }

   public void prepareForSave(Map<String, Serializable> actionProps,
         Map<String, Serializable> repoProps)
   {
      repoProps.put(TagActionExecuter.PARAM_TAGS, (String)actionProps.get(PROP_TAGS));
   }

   public void prepareForEdit(Map<String, Serializable> actionProps,
         Map<String, Serializable> repoProps)
   {
      actionProps.put(PROP_TAGS, (String)repoProps.get(TagActionExecuter.PARAM_TAGS));
   }

   public String generateSummary(FacesContext context, IWizardBean wizard,
         Map<String, Serializable> actionProps)
   {
      String tags = (String)actionProps.get(PROP_TAGS);
      if (tags == null)
      {
         tags = "";
      }
      
      return MessageFormat.format(Application.getMessage(context, "add_tags"), 
            new Object[] {tags});
   }
}
