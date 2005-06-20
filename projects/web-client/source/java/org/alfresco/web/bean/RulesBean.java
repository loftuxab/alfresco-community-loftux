package org.alfresco.web.bean;

import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.transaction.UserTransaction;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.service.cmr.rule.Rule;
import org.alfresco.service.cmr.rule.RuleService;
import org.alfresco.web.bean.repository.Node;
import org.alfresco.web.bean.repository.Repository;
import org.alfresco.web.ui.common.Utils;
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
   private Rule currentRule;
   
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
      // TODO: for now just get the local rules as inherited rules are not implemented yet
      this.rules = this.ruleService.getRules(this.browseBean.getActionSpace().getNodeRef());
      return this.rules;
   }
   
   /**
    * Handles a rule being clicked ready for an action i.e. edit or delete
    * 
    * @param event The event representing the click
    */
   public void setupRuleAction(ActionEvent event)
   {
      UIActionLink link = (UIActionLink)event.getComponent();
      Map<String, String> params = link.getParameterMap();
      String id = params.get("id");
      if (id != null && id.length() != 0)
      {
         if (logger.isDebugEnabled())
            logger.debug("Rule clicked, it's id is: " + id);
         
         this.currentRule = this.ruleService.getRule(
               this.browseBean.getActionSpace().getNodeRef(), id);
      }
   }
   
   /**
    * Returns the current rule 
    * 
    * @return The current rule
    */
   public Rule getCurrentRule()
   {
      return this.currentRule;
   }
   
   /**
    * Handler called upon the completion of the Delete Rule page
    * 
    * @return outcome
    */
   public String deleteOK()
   {
      String outcome = null;
      
      if (this.currentRule != null)
      {
         try
         {
            String ruleTitle = this.currentRule.getTitle();
            
            this.ruleService.removeRule(this.browseBean.getActionSpace().getNodeRef(),
                  this.currentRule);
            
            // clear the current rule
            this.currentRule = null;
            
            // setting the outcome will show the browse view again
            outcome = "manageRules";
            
            if (logger.isDebugEnabled())
               logger.debug("Deleted rule '" + ruleTitle + "'");
         }
         catch (Throwable err)
         {
            Utils.addErrorMessage("Unable to delete Rule due to system error: " + err.getMessage(), err);
         }
      }
      else
      {
         logger.warn("WARNING: deleteOK called without a current Rule!");
      }
      
      return outcome;
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
