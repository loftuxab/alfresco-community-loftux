/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the Mozilla Public License version 1.1 
 * with a permitted attribution clause. You may obtain a
 * copy of the License at
 *
 *   http://www.alfresco.org/legal/license.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.web.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ActionCondition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.rule.Rule;
import org.alfresco.service.cmr.rule.RuleService;
import org.alfresco.web.app.Application;
import org.alfresco.web.ui.common.Utils;
import org.alfresco.web.ui.common.component.UIActionLink;
import org.alfresco.web.ui.common.component.UIModeList;
import org.alfresco.web.ui.common.component.data.UIRichList;
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
   private static final String LOCAL = "local";
   private static final String INHERITED = "inherited";
   
   private static Log logger = LogFactory.getLog(RulesBean.class);
   
   private String viewMode = INHERITED;
   private BrowseBean browseBean;
   private RuleService ruleService;
   private List<WrappedRule> rules;
   private Rule currentRule;
   private UIRichList richList;
   
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
   public List<WrappedRule> getRules()
   {
      boolean includeInherited = true;
      
      if (this.viewMode.equals(LOCAL))
      {
         includeInherited = false;
      }

      // get the rules from the repository
      List<Rule> repoRules = this.ruleService.getRules(this.browseBean.getActionSpace().getNodeRef(), includeInherited);
      this.rules = new ArrayList<WrappedRule>(repoRules.size());
      
      // wrap them all passing the current space
      for (Rule rule : repoRules)
      {
         WrappedRule wrapped = new WrappedRule(rule, this.browseBean.getActionSpace().getNodeRef());
         this.rules.add(wrapped);
      }
      
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
      
      // force the list to be re-queried when the page is refreshed
      if (this.richList != null)
      {
         this.richList.setValue(null);
      }
   }

   /**
    * Sets the UIRichList component being used by this backing bean
    * 
    * @param richList UIRichList component
    */
   public void setRichList(UIRichList richList)
   {
      this.richList = richList;
      this.richList.setValue(null);
   }
   
   /**
    * Returns the UIRichList component being used by this backing bean
    * 
    * @return UIRichList component
    */
   public UIRichList getRichList()
   {
      return this.richList;
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
   
   /**
    * Inner class to wrap the Rule objects so we can expose a flag to indicate whether
    * the rule is a local or inherited rule
    */
   public class WrappedRule implements Rule
   {
      private Rule rule;
      private NodeRef ruleNode;
      
      /**
       * Constructs a RuleWrapper object
       * 
       * @param rule The rule we are wrapping
       * @param ruleNode The node the rules belong to 
       */
      public WrappedRule(Rule rule, NodeRef ruleNode)
      {
         this.rule = rule;
         this.ruleNode = ruleNode;
      }
      
      /**
       * Determines whether the current rule is a local rule or
       * has been inherited from a parent
       * 
       * @return true if the rule is defined on the current node
       */
      public boolean getLocal()
      {
         return ruleNode.equals(getOwningNodeRef());
      }

      /**
       * @see org.alfresco.service.cmr.action.Action#getOwningNodeRef()
       */
      public NodeRef getOwningNodeRef()
      {
         return this.rule.getOwningNodeRef();
      }
      
      /**
       * @see org.alfresco.service.cmr.rule.Rule#isAppliedToChildren()
       */
      public boolean isAppliedToChildren()
      {
         return this.rule.isAppliedToChildren();
      }

      /**
       * @see org.alfresco.service.cmr.rule.Rule#applyToChildren(boolean)
       */
      public void applyToChildren(boolean isAppliedToChildren)
      {
         this.rule.applyToChildren(isAppliedToChildren);
      }

      /**
       * @see org.alfresco.service.cmr.rule.Rule#getRuleTypeName()
       */
      public String getRuleTypeName()
      {
         return this.rule.getRuleTypeName();
      }

      /**
       * @see org.alfresco.service.cmr.action.CompositeAction#hasActions()
       */
      public boolean hasActions()
      {
         return this.rule.hasActions();
      }

      /**
       * @see org.alfresco.service.cmr.action.CompositeAction#addAction(org.alfresco.service.cmr.action.Action)
       */
      public void addAction(Action action)
      {
         this.rule.addAction(action);
      }

      /**
       * @see org.alfresco.service.cmr.action.CompositeAction#addAction(int, org.alfresco.service.cmr.action.Action)
       */
      public void addAction(int index, Action action)
      {
         this.rule.addAction(index, action);
      }

      /**
       * @see org.alfresco.service.cmr.action.CompositeAction#setAction(int, org.alfresco.service.cmr.action.Action)
       */
      public void setAction(int index, Action action)
      {
         this.rule.setAction(index, action);
      }

      /**
       * @see org.alfresco.service.cmr.action.CompositeAction#indexOfAction(org.alfresco.service.cmr.action.Action)
       */
      public int indexOfAction(Action action)
      {
         return this.rule.indexOfAction(action);
      }

      /**
       * @see org.alfresco.service.cmr.action.CompositeAction#getActions()
       */
      public List<Action> getActions()
      {
         return this.rule.getActions();
      }

      /**
       * @see org.alfresco.service.cmr.action.CompositeAction#getAction(int)
       */
      public Action getAction(int index)
      {
         return this.rule.getAction(index);
      }

      /**
       * @see org.alfresco.service.cmr.action.CompositeAction#removeAction(org.alfresco.service.cmr.action.Action)
       */
      public void removeAction(Action action)
      {
         this.rule.removeAction(action);
      }

      /**
       * @see org.alfresco.service.cmr.action.CompositeAction#removeAllActions()
       */
      public void removeAllActions()
      {
         this.rule.removeAllActions();
      }

      /**
       * @see org.alfresco.service.cmr.action.Action#getActionDefinitionName()
       */
      public String getActionDefinitionName()
      {
         return this.rule.getActionDefinitionName();
      }

      /**
       * @see org.alfresco.service.cmr.action.Action#getTitle()
       */
      public String getTitle()
      {
         return this.rule.getTitle();
      }

      /**
       * @see org.alfresco.service.cmr.action.Action#setTitle(java.lang.String)
       */
      public void setTitle(String title)
      {
         this.rule.setTitle(title);
      }

      /**
       * @see org.alfresco.service.cmr.action.Action#getDescription()
       */
      public String getDescription()
      {
         return this.rule.getDescription();
      }

      /**
       * @see org.alfresco.service.cmr.action.Action#setDescription(java.lang.String)
       */
      public void setDescription(String description)
      {
         this.rule.setDescription(description);
      }

      /**
       * @see org.alfresco.service.cmr.action.Action#getExecuteAsychronously()
       */
      public boolean getExecuteAsychronously()
      {
         return this.rule.getExecuteAsychronously();
      }

      /**
       * @see org.alfresco.service.cmr.action.Action#setExecuteAsynchronously(boolean)
       */
      public void setExecuteAsynchronously(boolean executeAsynchronously)
      {
         this.rule.setExecuteAsynchronously(executeAsynchronously);
      }

      /**
       * @see org.alfresco.service.cmr.action.Action#getCompensatingAction()
       */
      public Action getCompensatingAction()
      {
         return this.rule.getCompensatingAction();
      }

      /**
       * @see org.alfresco.service.cmr.action.Action#setCompensatingAction(org.alfresco.service.cmr.action.Action)
       */
      public void setCompensatingAction(Action action)
      {
         this.rule.setCompensatingAction(action);
      }

      /**
       * @see org.alfresco.service.cmr.action.Action#getCreatedDate()
       */
      public Date getCreatedDate()
      {
         return this.rule.getCreatedDate();
      }

      /**
       * @see org.alfresco.service.cmr.action.Action#getCreator()
       */
      public String getCreator()
      {
         return this.rule.getCreator();
      }

      /**
       * @see org.alfresco.service.cmr.action.Action#getModifiedDate()
       */
      public Date getModifiedDate()
      {
         return this.rule.getModifiedDate();
      }

      /**
       * @see org.alfresco.service.cmr.action.Action#getModifier()
       */
      public String getModifier()
      {
         return this.rule.getModifier();
      }

      /**
       * @see org.alfresco.service.cmr.action.Action#hasActionConditions()
       */
      public boolean hasActionConditions()
      {
         return this.rule.hasActionConditions();
      }

      /**
       * @see org.alfresco.service.cmr.action.Action#indexOfActionCondition(org.alfresco.service.cmr.action.ActionCondition)
       */
      public int indexOfActionCondition(ActionCondition actionCondition)
      {
         return this.rule.indexOfActionCondition(actionCondition);
      }

      /**
       * @see org.alfresco.service.cmr.action.Action#getActionConditions()
       */
      public List<ActionCondition> getActionConditions()
      {
         return this.rule.getActionConditions();
      }

      /**
       * @see org.alfresco.service.cmr.action.Action#getActionCondition(int)
       */
      public ActionCondition getActionCondition(int index)
      {
         return this.rule.getActionCondition(index);
      }

      /**
       * @see org.alfresco.service.cmr.action.Action#addActionCondition(org.alfresco.service.cmr.action.ActionCondition)
       */
      public void addActionCondition(ActionCondition actionCondition)
      {
         this.rule.addActionCondition(actionCondition);
      }

      /**
       * @see org.alfresco.service.cmr.action.Action#addActionCondition(int, org.alfresco.service.cmr.action.ActionCondition)
       */
      public void addActionCondition(int index, ActionCondition actionCondition)
      {
         this.rule.addActionCondition(index, actionCondition);
      }

      /**
       * @see org.alfresco.service.cmr.action.Action#setActionCondition(int, org.alfresco.service.cmr.action.ActionCondition)
       */
      public void setActionCondition(int index, ActionCondition actionCondition)
      {
         this.rule.setActionCondition(index, actionCondition);
      }

      /**
       * @see org.alfresco.service.cmr.action.Action#removeActionCondition(org.alfresco.service.cmr.action.ActionCondition)
       */
      public void removeActionCondition(ActionCondition actionCondition)
      {
         this.rule.removeActionCondition(actionCondition);
      }

      /**
       * @see org.alfresco.service.cmr.action.Action#removeAllActionConditions()
       */
      public void removeAllActionConditions()
      {
         this.rule.removeAllActionConditions();
      }

      /**
       * @see org.alfresco.service.cmr.action.ParameterizedItem#getId()
       */
      public String getId()
      {
         return this.rule.getId();
      }

      /**
       * @see org.alfresco.service.cmr.action.ParameterizedItem#getParameterValues()
       */
      public Map<String, Serializable> getParameterValues()
      {
         return this.rule.getParameterValues();
      }

      /**
       * @see org.alfresco.service.cmr.action.ParameterizedItem#getParameterValue(java.lang.String)
       */
      public Serializable getParameterValue(String name)
      {
         return this.rule.getParameterValue(name);
      }

      /**
       * @see org.alfresco.service.cmr.action.ParameterizedItem#setParameterValues(java.util.Map)
       */
      public void setParameterValues(Map<String, Serializable> parameterValues)
      {
         this.rule.setParameterValues(parameterValues);
      }

      /**
       * @see org.alfresco.service.cmr.action.ParameterizedItem#setParameterValue(java.lang.String, java.io.Serializable)
       */
      public void setParameterValue(String name, Serializable value)
      {
         this.rule.setParameterValue(name, value);
      }
   }
}
