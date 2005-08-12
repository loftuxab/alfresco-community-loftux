/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/lgpl.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.web.bean;

import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.alfresco.service.cmr.rule.Rule;
import org.alfresco.service.cmr.rule.RuleService;
import org.alfresco.web.app.Application;
import org.alfresco.web.ui.common.Utils;
import org.alfresco.web.ui.common.component.UIActionLink;
import org.alfresco.web.ui.common.component.UIModeList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Backing bean for the manage content rules dialog
 *  
 * @author gavinc
 */
public class RulesBean
{
   private static final String MSG_ERROR_DELETE_RULE = "error_delete_rule";

   private static Log logger = LogFactory.getLog(RulesBean.class);
   
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
            Utils.addErrorMessage(Application.getMessage(
                  FacesContext.getCurrentInstance(), MSG_ERROR_DELETE_RULE) + err.getMessage(), err);
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
