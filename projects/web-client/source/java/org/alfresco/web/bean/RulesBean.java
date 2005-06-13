package org.alfresco.web.bean;

import java.util.List;
import java.util.Map;

import javax.faces.event.ActionEvent;

import org.alfresco.service.cmr.rule.Rule;
import org.alfresco.service.cmr.rule.RuleService;
import org.alfresco.web.ui.common.component.UIActionLink;
import org.alfresco.web.ui.common.component.UIModeList;
import org.apache.log4j.Logger;

/**
 * Backing bean for the manage content rules dialog
 *  
 * @author gavinc
 */
public class RulesBean
{
   private static Logger logger = Logger.getLogger(RulesBean.class);
   
   private String viewMode = "local";
   private BrowseBean browseBean;
   private RuleService ruleService;
   private List<Rule> rules;
   
   /**
    * Returns the current view mode the list of rules is in
    * 
    * @return The current view mode
    */
   public String getViewMode()
   {
      return this.viewMode;
   }
   
   /**
    * Returns the list of rules to display
    * 
    * @return
    */
   public List<Rule> getRules()
   {
      if (this.viewMode.equals("local"))
      {
         this.rules = this.ruleService.getRules(this.browseBean.getActionSpace().getNodeRef(), false);
      }
      else
      {
         this.rules = this.ruleService.getRules(this.browseBean.getActionSpace().getNodeRef(), true);
      }
      
      return this.rules;
   }
   
   /**
    * Handles a rule being clicked
    * 
    * @param event The event representing the click
    */
   public void clickRule(ActionEvent event)
   {
      UIActionLink link = (UIActionLink)event.getComponent();
      Map<String, String> params = link.getParameterMap();
      String id = params.get("id");
      if (id != null && id.length() != 0)
      {
         if (logger.isDebugEnabled())
            logger.debug("Rule clicked, it's id is: " + id);
      }
      else
      {
         if (logger.isDebugEnabled())
            logger.debug("No id set for rule!!!!");
      }
   }
   
   /**
    * Change the current view mode based on user selection
    * 
    * @param event      ActionEvent
    */
   public void viewModeChanged(ActionEvent event)
   {
      UIModeList viewList = (UIModeList)event.getComponent();
      this.viewMode = viewList.getValue().toString();
   }

   /**
    * @param browseBean The BrowseBean to set.
    */
   public void setBrowseBean(BrowseBean browseBean)
   {
      this.browseBean = browseBean;
   }
   
   /**
    * @param ruleService Sets the rule service to use
    */
   public void setRuleService(RuleService ruleService)
   {
      this.ruleService = ruleService;
   }
}
