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
package org.alfresco.web.bean.wizard;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.transaction.UserTransaction;

import org.alfresco.config.Config;
import org.alfresco.config.ConfigElement;
import org.alfresco.config.ConfigService;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.rule.action.AddFeaturesActionExecuter;
import org.alfresco.repo.rule.action.CheckInActionExecuter;
import org.alfresco.repo.rule.action.CheckOutActionExecuter;
import org.alfresco.repo.rule.action.CopyActionExecuter;
import org.alfresco.repo.rule.action.LinkCategoryActionExecuter;
import org.alfresco.repo.rule.action.MailActionExecuter;
import org.alfresco.repo.rule.action.MoveActionExecuter;
import org.alfresco.repo.rule.action.SimpleWorkflowActionExecuter;
import org.alfresco.repo.rule.action.TransformActionExecuter;
import org.alfresco.repo.rule.condition.InCategoryEvaluator;
import org.alfresco.repo.rule.condition.MatchTextEvaluator;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.rule.Rule;
import org.alfresco.service.cmr.rule.RuleActionDefinition;
import org.alfresco.service.cmr.rule.RuleConditionDefinition;
import org.alfresco.service.cmr.rule.RuleService;
import org.alfresco.service.cmr.rule.RuleType;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.web.app.Application;
import org.alfresco.web.bean.RulesBean;
import org.alfresco.web.bean.repository.Node;
import org.alfresco.web.bean.repository.Repository;
import org.alfresco.web.data.IDataContainer;
import org.alfresco.web.data.QuickSort;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.jsf.FacesContextUtils;

/**
 * Handler class used by the New Space Wizard 
 * 
 * @author gavinc
 */
public class NewRuleWizard extends AbstractWizardBean
{
   // parameter names for conditions and actions
   public static final String PROP_CONTAINS_TEXT = "containstext";
   public static final String PROP_CATEGORY = "category";
   public static final String PROP_ASPECT = "aspect";
   public static final String PROP_DESTINATION = "destinationLocation";
   public static final String PROP_APPROVE_STEP_NAME = "approveStepName";
   public static final String PROP_APPROVE_ACTION = "approveAction";
   public static final String PROP_APPROVE_FOLDER = "approveFolder";
   public static final String PROP_REJECT_STEP_PRESENT = "rejectStepPresent";
   public static final String PROP_REJECT_STEP_NAME = "rejectStepName";
   public static final String PROP_REJECT_ACTION = "rejectAction";
   public static final String PROP_REJECT_FOLDER = "rejectFolder";
   public static final String PROP_CHECKIN_DESC = "checkinDescription";
   public static final String PROP_TRANSFORMER = "transformer";
   public static final String PROP_IMAGE_TRANSFORMER = "imageTransformer";
   public static final String PROP_IMAGE_TRANSFORM_DESC = "imageTransformDesc";
   public static final String PROP_MESSAGE = "message";
   public static final String PROP_SUBJECT = "subject";
   public static final String PROP_TO = "to";
   
   private static Log logger = LogFactory.getLog(NewRuleWizard.class);
   
   // TODO: retrieve these from the config service
   private static final String WIZARD_TITLE = "New Rule Wizard";
   private static final String WIZARD_TITLE_EDIT = "Edit Rule Wizard";
   private static final String WIZARD_DESC = "This wizard helps you create a new rule.";
   private static final String WIZARD_DESC_EDIT = "This wizard helps you modify a rule.";
   private static final String STEP1_TITLE = "Step One - Enter Details";
   private static final String STEP2_TITLE = "Step Two - Select Condition";
   private static final String STEP3_TITLE = "Step Three - Condition Settings";
   private static final String STEP4_TITLE = "Step Four - Select Action";
   private static final String STEP5_TITLE = "Step Five - Action Settings";
   private static final String FINISH_INSTRUCTION = "To create the rule click Finish.";
   private static final String FINISH_INSTRUCTION_EDIT = "To update the rule click Finish.";
   
   // new rule wizard specific properties
   private String title;
   private String description;
   private String type;
   private String condition;
   private String action;
   private RuleService ruleService;
   private RulesBean rulesBean;
   private List<SelectItem> types;
   private List<SelectItem> conditions;
   private List<SelectItem> actions;
   private List<SelectItem> transformers;
   private List<SelectItem> imageTransformers;
   private List<SelectItem> aspects;
   private List<SelectItem> users;
   private Map<String, String> conditionDescriptions;
   private Map<String, String> actionDescriptions;
   private Map<String, String> conditionProperties;
   private Map<String, String> actionProperties;
   
   /**
    * Deals with the finish button being pressed
    * 
    * @return outcome
    */
   public String finish()
   {
      String outcome = FINISH_OUTCOME;
      
      UserTransaction tx = null;
   
      try
      {
         FacesContext context = FacesContext.getCurrentInstance();
         tx = Repository.getUserTransaction(FacesContext.getCurrentInstance());
         tx.begin();
         
         // set up parameters maps for the condition
         Map<String, Serializable> conditionParams = new HashMap<String, Serializable>();
         if (this.condition.equals(MatchTextEvaluator.NAME))
         {
            conditionParams.put(MatchTextEvaluator.PARAM_TEXT, 
                  this.conditionProperties.get(PROP_CONTAINS_TEXT));
         }
         else if (this.condition.equals(InCategoryEvaluator.NAME))
         {
            // put the selected category in the condition params
            NodeRef catNodeRef = new NodeRef(Repository.getStoreRef(), 
                  this.conditionProperties.get(PROP_CATEGORY));
            conditionParams.put(InCategoryEvaluator.PARAM_CATEGORY_VALUE, catNodeRef);
            
            // add the classifiable aspect
            conditionParams.put(InCategoryEvaluator.PARAM_CATEGORY_ASPECT, ContentModel.ASPECT_GEN_CLASSIFIABLE);
         }
         
         // set up parameters maps for the action
         Map<String, Serializable> actionParams = new HashMap<String, Serializable>();
         if (this.action.equals(AddFeaturesActionExecuter.NAME))
         {
            QName aspect = Repository.resolveToQName(this.actionProperties.get(PROP_ASPECT));
            actionParams.put(AddFeaturesActionExecuter.PARAM_ASPECT_NAME, aspect);
         }
         else if (this.action.equals(CopyActionExecuter.NAME))
         {
            // add the destination space id to the action properties
            NodeRef destNodeRef = new NodeRef(Repository.getStoreRef(), 
                  this.actionProperties.get(PROP_DESTINATION));
            actionParams.put(CopyActionExecuter.PARAM_DESTINATION_FOLDER, destNodeRef);
            
            // add the type and name of the association to create when the copy
            // is performed
            actionParams.put(CopyActionExecuter.PARAM_ASSOC_TYPE_QNAME, 
                  ContentModel.ASSOC_CONTAINS);
            actionParams.put(CopyActionExecuter.PARAM_ASSOC_QNAME, 
                  QName.createQName(NamespaceService.ALFRESCO_URI, "copy"));
         }
         else if (this.action.equals(MoveActionExecuter.NAME))
         {
            // add the destination space id to the action properties
            NodeRef destNodeRef = new NodeRef(Repository.getStoreRef(), 
                  this.actionProperties.get(PROP_DESTINATION));
            actionParams.put(MoveActionExecuter.PARAM_DESTINATION_FOLDER, destNodeRef);
            
            // add the type and name of the association to create when the move
            // is performed
            actionParams.put(MoveActionExecuter.PARAM_ASSOC_TYPE_QNAME, 
                  ContentModel.ASSOC_CONTAINS);
            actionParams.put(MoveActionExecuter.PARAM_ASSOC_QNAME, 
                  QName.createQName(NamespaceService.ALFRESCO_URI, "move"));
         }
         else if (this.action.equals(SimpleWorkflowActionExecuter.NAME))
         {
            // add the approve step name
            actionParams.put(SimpleWorkflowActionExecuter.PARAM_APPROVE_STEP,
                  this.actionProperties.get(PROP_APPROVE_STEP_NAME));
            
            // add whether the approve step will copy or move the content
            boolean approveMove = true;
            String approveAction = this.actionProperties.get(PROP_APPROVE_ACTION);
            if (approveAction != null && approveAction.equals("copy"))
            {
               approveMove = false;
            }
            
            actionParams.put(SimpleWorkflowActionExecuter.PARAM_APPROVE_MOVE,
                  new Boolean(approveMove));
            
            // add the destination folder of the content
            NodeRef approveDestNodeRef = new NodeRef(Repository.getStoreRef(), 
                  this.actionProperties.get(PROP_APPROVE_FOLDER));
            actionParams.put(SimpleWorkflowActionExecuter.PARAM_APPROVE_FOLDER, 
                  approveDestNodeRef);
            
            // determine whether we have a reject step or not
            boolean requireReject = true;
            String rejectStepPresent = this.actionProperties.get(PROP_REJECT_STEP_PRESENT);
            if (rejectStepPresent != null && rejectStepPresent.equals("no"))
            {
               requireReject = false;
            }

            if (requireReject)
            {
               // add the reject step name
               actionParams.put(SimpleWorkflowActionExecuter.PARAM_REJECT_STEP,
                     this.actionProperties.get(PROP_REJECT_STEP_NAME));
            
               // add whether the reject step will copy or move the content
               boolean rejectMove = true;
               String rejectAction = this.actionProperties.get(PROP_REJECT_ACTION);
               if (rejectAction != null && rejectAction.equals("copy"))
               {
                  rejectMove = false;
               }
               
               actionParams.put(SimpleWorkflowActionExecuter.PARAM_REJECT_MOVE,
                     new Boolean(rejectMove));
               
               // add the destination folder of the content
               NodeRef rejectDestNodeRef = new NodeRef(Repository.getStoreRef(), 
                     this.actionProperties.get(PROP_REJECT_FOLDER));
               actionParams.put(SimpleWorkflowActionExecuter.PARAM_REJECT_FOLDER, 
                     rejectDestNodeRef);
            }
         }
         else if (this.action.equals(LinkCategoryActionExecuter.NAME))
         {
            // add the classifiable aspect
            actionParams.put(LinkCategoryActionExecuter.PARAM_CATEGORY_ASPECT,
                  ContentModel.ASPECT_GEN_CLASSIFIABLE);
            
            // put the selected category in the action params
            NodeRef catNodeRef = new NodeRef(Repository.getStoreRef(), 
                  this.actionProperties.get(PROP_CATEGORY));
            actionParams.put(LinkCategoryActionExecuter.PARAM_CATEGORY_VALUE, 
                  catNodeRef);
         }
         else if (this.action.equals(CheckOutActionExecuter.NAME))
         {
            // specify the location the checked out working copy should go
            // add the destination space id to the action properties
            NodeRef destNodeRef = new NodeRef(Repository.getStoreRef(), 
                  this.actionProperties.get(PROP_DESTINATION));
            actionParams.put(CheckOutActionExecuter.PARAM_DESTINATION_FOLDER, destNodeRef);
            
            // add the type and name of the association to create when the 
            // check out is performed
            actionParams.put(CheckOutActionExecuter.PARAM_ASSOC_TYPE_QNAME, 
                  ContentModel.ASSOC_CONTAINS);
            actionParams.put(CheckOutActionExecuter.PARAM_ASSOC_QNAME, 
                  QName.createQName(NamespaceService.ALFRESCO_URI, "checkout"));
         }
         else if (this.action.equals(CheckInActionExecuter.NAME))
         {
            // add the description for the checkin to the action params
            actionParams.put(CheckInActionExecuter.PARAM_DESCRIPTION, 
                  this.actionProperties.get(PROP_CHECKIN_DESC));
         }
         else if (this.action.equals(TransformActionExecuter.NAME))
         {
            // add the transformer to use
            actionParams.put(TransformActionExecuter.PARAM_MIME_TYPE,
                  this.actionProperties.get(PROP_TRANSFORMER));
            
            // add the destination space id to the action properties
            NodeRef destNodeRef = new NodeRef(Repository.getStoreRef(), 
                  this.actionProperties.get(PROP_DESTINATION));
            actionParams.put(TransformActionExecuter.PARAM_DESTINATION_FOLDER, destNodeRef);
            
            // add the type and name of the association to create when the copy
            // is performed
            actionParams.put(TransformActionExecuter.PARAM_ASSOC_TYPE_QNAME, 
                  ContentModel.ASSOC_CONTAINS);
            actionParams.put(TransformActionExecuter.PARAM_ASSOC_QNAME, 
                  QName.createQName(NamespaceService.ALFRESCO_URI, "copy"));
         }
         else if (this.action.equals("transform-image"))
         {
            // add the transformer to use
            actionParams.put(TransformActionExecuter.PARAM_MIME_TYPE,
                  this.actionProperties.get(PROP_IMAGE_TRANSFORMER));
            
            // add the description
            // TODO: Use the proper description parameter when Roy adds it
            //actionParams.put("description", 
            //      this.actionProperties.get(PROP_IMAGE_TRANSFORM_DESC));
            
            // add the destination space id to the action properties
            NodeRef destNodeRef = new NodeRef(Repository.getStoreRef(), 
                  this.actionProperties.get(PROP_DESTINATION));
            actionParams.put(TransformActionExecuter.PARAM_DESTINATION_FOLDER, destNodeRef);
            
            // add the type and name of the association to create when the copy
            // is performed
            actionParams.put(TransformActionExecuter.PARAM_ASSOC_TYPE_QNAME, 
                  ContentModel.ASSOC_CONTAINS);
            actionParams.put(TransformActionExecuter.PARAM_ASSOC_QNAME, 
                  QName.createQName(NamespaceService.ALFRESCO_URI, "copy"));
         }
         else if (this.action.equals(MailActionExecuter.NAME))
         {
            // add the actual email text to send
            actionParams.put(MailActionExecuter.PARAM_TEXT, 
                  this.actionProperties.get(PROP_MESSAGE));
               
            // add the person it's going to
            actionParams.put(MailActionExecuter.PARAM_TO, 
                  this.actionProperties.get(PROP_TO));
            
            // add the subject for the email
            actionParams.put(MailActionExecuter.PARAM_SUBJECT,
                  this.actionProperties.get(PROP_SUBJECT));
         }

         // get the definition for the selected condition and action
         Rule rule = null;
         RuleConditionDefinition cond = this.ruleService.getConditionDefinition(this.getCondition());
         RuleActionDefinition action = this.ruleService.getActionDefinition(this.getAction());
         
         // get hold of the space the rule will apply to and make sure
         // it is actionable
         Node currentSpace = browseBean.getActionSpace();
         if (this.ruleService.isActionable(currentSpace.getNodeRef()) == false)
         {
            this.ruleService.makeActionable(currentSpace.getNodeRef());
         }

         if (this.editMode)
         {
            // update the existing rule in the repository
            rule = this.rulesBean.getCurrentRule();
            
            // we know there is only one condition and action
            // so remove the first one
            rule.removeRuleCondition(rule.getRuleConditions().get(0));
            rule.removeRuleAction(rule.getRuleActions().get(0));
         }
         else
         {
            RuleType ruleType = this.ruleService.getRuleType(this.getType());
            rule = this.ruleService.createRule(ruleType);
         }

         // setup the rule and add it to the space
         rule.setTitle(this.title);
         rule.setDescription(this.description);
         rule.addRuleCondition(cond, conditionParams);
         rule.addRuleAction(action, actionParams);
         this.ruleService.addRule(currentSpace.getNodeRef(), rule);
         
         if (logger.isDebugEnabled())
         {
            logger.debug(this.editMode ? "Updated" : "Added" 
                         + " rule '" + this.title + "' with condition '" + 
                         this.condition + "', action '" + this.action + 
                         "', condition params of " +
                         this.conditionProperties + " and action params of " + 
                         this.actionProperties);
         }
         
         // commit the transaction
         tx.commit();
      }
      catch (Exception e)
      {
         // rollback the transaction
         try { if (tx != null) {tx.rollback();} } catch (Exception ex) {}
         throw new AlfrescoRuntimeException("Failed to create new rule", e);
      }
      
      return outcome;
   }

   /**
    * @see org.alfresco.web.bean.wizard.AbstractWizardBean#next()
    */
   public String next()
   {
      String outcome = super.next();
      
      // if the outcome is "no-condition" we must move the step counter
      // on as there are no settings for "no-condition"
      if (outcome.equals("no-condition"))
      {
         this.currentStep++;
         
         if (logger.isDebugEnabled())
            logger.debug("current step is now " + this.currentStep + 
                         " as there are no settings associated with the selected condition");
      }
      
      return outcome;
   }
   
   /**
    * @see org.alfresco.web.bean.wizard.AbstractWizardBean#back()
    */
   public String back()
   {
      String outcome = super.back();
      
      // if the outcome is "no-condition" we must move the step counter
      // back as there are no settings for "no-condition"
      if (outcome.equals("no-condition"))
      {
         this.currentStep--;
         
         if (logger.isDebugEnabled())
            logger.debug("current step is now " + this.currentStep + 
                         " as there are no settings associated with the selected condition");
      }
      
      return outcome;
   }

   /**
    * @see org.alfresco.web.bean.wizard.AbstractWizardBean#getWizardDescription()
    */
   public String getWizardDescription()
   {
      if (this.editMode)
      {
         return WIZARD_DESC_EDIT;
      }
      else
      {
         return WIZARD_DESC;
      }
   }

   /**
    * @see org.alfresco.web.bean.wizard.AbstractWizardBean#getWizardTitle()
    */
   public String getWizardTitle()
   {
      if (this.editMode)
      {
         return WIZARD_TITLE_EDIT;
      }
      else
      {
         return WIZARD_TITLE;
      }
   }
   
   /**
    * @see org.alfresco.web.bean.wizard.AbstractWizardBean#getStepDescription()
    */
   public String getStepDescription()
   {
      String stepDesc = null;
      
      switch (this.currentStep)
      {
         case 6:
         {
            stepDesc = SUMMARY_DESCRIPTION;
            break;
         }
         default:
         {
            stepDesc = "";
         }
      }
      
      return stepDesc;
   }

   /**
    * @see org.alfresco.web.bean.wizard.AbstractWizardBean#getStepTitle()
    */
   public String getStepTitle()
   {
      String stepTitle = null;
      
      switch (this.currentStep)
      {
         case 1:
         {
            stepTitle = STEP1_TITLE;
            break;
         }
         case 2:
         {
            stepTitle = STEP2_TITLE;
            break;
         }
         case 3:
         {
            stepTitle = STEP3_TITLE;
            break;
         }
         case 4:
         {
            stepTitle = STEP4_TITLE;
            break;
         }
         case 5:
         {
            stepTitle = STEP5_TITLE;
            break;
         }
         case 6:
         {
            stepTitle = SUMMARY_TITLE;
            break;
         }
         default:
         {
            stepTitle = "";
         }
      }
      
      return stepTitle;
   }
   
   /**
    * @see org.alfresco.web.bean.wizard.AbstractWizardBean#getStepInstructions()
    */
   public String getStepInstructions()
   {
      String stepInstruction = null;
      
      switch (this.currentStep)
      {
         case 5:
         {
            if (this.editMode)
            {
               stepInstruction = FINISH_INSTRUCTION_EDIT;
            }
            else
            {
               stepInstruction = FINISH_INSTRUCTION;
            }
            break;
         }
         case 6:
         {
            if (this.editMode)
            {
               stepInstruction = FINISH_INSTRUCTION_EDIT;
            }
            else
            {
               stepInstruction = FINISH_INSTRUCTION;
            }
            break;
         }
         default:
         {
            stepInstruction = DEFAULT_INSTRUCTION;
         }
      }
      
      return stepInstruction;
   }
   
   /**
    * Initialises the wizard
    */
   public void init()
   {
      super.init();
      
      this.title = null;
      this.description = null;
      this.type = "inbound";
      this.action = "add-features";
      this.condition = "no-condition";
      
      if (this.users != null)
      {
         this.users.clear();
         this.users = null;
      }
      
      if (this.actions != null)
      {
         this.actions.clear();
         this.actions = null;
      }
      
      if (this.conditions != null)
      {
         this.conditions.clear();
         this.conditions = null;
      }
      
      if (this.conditionDescriptions != null)
      {
         this.conditionDescriptions.clear();
         this.conditionDescriptions = null;
      }
      
      if (this.actionDescriptions != null)
      {
         this.actionDescriptions.clear();
         this.actionDescriptions = null;
      }
      
      this.conditionProperties = new HashMap<String, String>(1);
      this.actionProperties = new HashMap<String, String>(3);
      
      // default the approve and reject actions
      this.actionProperties.put("approveAction", "move");
      this.actionProperties.put("rejectStepPresent", "yes");
      this.actionProperties.put("rejectAction", "move");
   }
   
   /**
    * Sets the context of the rule up before performing the 
    * standard wizard editing steps
    *  
    * @see org.alfresco.web.bean.wizard.AbstractWizardBean#startWizardForEdit(javax.faces.event.ActionEvent)
    */
   public void startWizardForEdit(ActionEvent event)
   {
      // setup context for rule to be edited
      this.rulesBean.setupRuleAction(event);
      
      // perform the usual edit processing
      super.startWizardForEdit(event);
   }

   /**
    * Populates the values of the backing bean ready for editing the rule
    * 
    * @see org.alfresco.web.bean.wizard.AbstractWizardBean#populate()
    */
   public void populate()
   {
      // get hold of the current rule details
      Rule rule = this.rulesBean.getCurrentRule();
      
      if (rule == null)
      {
         throw new AlfrescoRuntimeException("Failed to locate the current rule");
      }
      
      // populate the bean with current values 
      this.type = rule.getRuleType().getName();
      this.title = rule.getTitle();
      this.description = rule.getDescription();
      // we know there is only 1 condition and action
      this.condition = rule.getRuleConditions().get(0).getRuleConditionDefinition().getName();
      this.action = rule.getRuleActions().get(0).getRuleActionDefinition().getName();
      
      // populate the condition property bag with the relevant values
      Map<String, Serializable> condProps = rule.getRuleConditions().get(0).getParameterValues();
      if (this.condition.equals(MatchTextEvaluator.NAME))
      {
         this.conditionProperties.put(PROP_CONTAINS_TEXT, 
               (String)condProps.get(MatchTextEvaluator.PARAM_TEXT));
      }
      else if (this.condition.equals(InCategoryEvaluator.NAME))
      {
         NodeRef catNodeRef = (NodeRef)condProps.get(InCategoryEvaluator.PARAM_CATEGORY_VALUE);
         this.conditionProperties.put(PROP_CATEGORY, catNodeRef.getId());
      }
      
      // populate the action property bag with the relevant values
      Map<String, Serializable> actionProps = rule.getRuleActions().get(0).getParameterValues();
      if (this.action.equals(AddFeaturesActionExecuter.NAME))
      {
         QName aspect = (QName)actionProps.get(AddFeaturesActionExecuter.PARAM_ASPECT_NAME);
         this.actionProperties.put(PROP_ASPECT, aspect.getLocalName());
      }
      else if (this.action.equals(CopyActionExecuter.NAME))
      {
         NodeRef destNodeRef = (NodeRef)actionProps.get(CopyActionExecuter.PARAM_DESTINATION_FOLDER);
         this.actionProperties.put(PROP_DESTINATION, destNodeRef.getId());
      }
      else if (this.action.equals(MoveActionExecuter.NAME))
      {
         NodeRef destNodeRef = (NodeRef)actionProps.get(MoveActionExecuter.PARAM_DESTINATION_FOLDER);
         this.actionProperties.put(PROP_DESTINATION, destNodeRef.getId());
      }
      else if (this.action.equals(SimpleWorkflowActionExecuter.NAME))
      {
         String approveStep = (String)actionProps.get(SimpleWorkflowActionExecuter.PARAM_APPROVE_STEP);
         Boolean approveMove = (Boolean)actionProps.get(SimpleWorkflowActionExecuter.PARAM_APPROVE_MOVE);
         NodeRef approveFolderNode = (NodeRef)actionProps.get(
               SimpleWorkflowActionExecuter.PARAM_APPROVE_FOLDER);
         
         String rejectStep = (String)actionProps.get(SimpleWorkflowActionExecuter.PARAM_REJECT_STEP);
         Boolean rejectMove = (Boolean)actionProps.get(SimpleWorkflowActionExecuter.PARAM_REJECT_MOVE);
         NodeRef rejectFolderNode = (NodeRef)actionProps.get(
               SimpleWorkflowActionExecuter.PARAM_REJECT_FOLDER);
         
         this.actionProperties.put(PROP_APPROVE_STEP_NAME, approveStep);
         this.actionProperties.put(PROP_APPROVE_ACTION, approveMove ? "move" : "copy");
         this.actionProperties.put(PROP_APPROVE_FOLDER, approveFolderNode.getId());
         
         if (rejectStep == null && rejectMove == null && rejectFolderNode == null)
         {
            this.actionProperties.put(PROP_REJECT_STEP_PRESENT, "no");
         }
         else
         {
            this.actionProperties.put(PROP_REJECT_STEP_PRESENT, "yes");
            this.actionProperties.put(PROP_REJECT_STEP_NAME, rejectStep);
            this.actionProperties.put(PROP_REJECT_ACTION, rejectMove ? "move" : "copy");
            this.actionProperties.put(PROP_REJECT_FOLDER, rejectFolderNode.getId());
         }
      }
      else if (this.action.equals(LinkCategoryActionExecuter.NAME))
      {
         NodeRef catNodeRef = (NodeRef)actionProps.get(LinkCategoryActionExecuter.PARAM_CATEGORY_VALUE);
         this.actionProperties.put(PROP_CATEGORY, catNodeRef.getId());
      }
      else if (this.action.equals(CheckOutActionExecuter.NAME))
      {
         NodeRef destNodeRef = (NodeRef)actionProps.get(CheckOutActionExecuter.PARAM_DESTINATION_FOLDER);
         this.actionProperties.put(PROP_DESTINATION, destNodeRef.getId());
      }
      else if (this.action.equals(CheckInActionExecuter.NAME))
      {
         String checkDesc = (String)actionProps.get(CheckInActionExecuter.PARAM_DESCRIPTION);
         this.actionProperties.put(PROP_CHECKIN_DESC, checkDesc);
      }
      else if (this.action.equals(TransformActionExecuter.NAME))
      {
         String transformer = (String)actionProps.get(TransformActionExecuter.PARAM_MIME_TYPE);
         this.actionProperties.put(PROP_TRANSFORMER, transformer);
         
         NodeRef destNodeRef = (NodeRef)actionProps.get(CopyActionExecuter.PARAM_DESTINATION_FOLDER);
         this.actionProperties.put(PROP_DESTINATION, destNodeRef.getId());
      }
      else if (this.action.equals("transform-image"))
      {
         String transformer = (String)actionProps.get(TransformActionExecuter.PARAM_MIME_TYPE);
         this.actionProperties.put(PROP_IMAGE_TRANSFORMER, transformer);
         
         // TODO: use the proper parameter defined by Roy
         String desc = (String)actionProps.get("description");
         this.actionProperties.put(PROP_IMAGE_TRANSFORM_DESC, desc);
         
         NodeRef destNodeRef = (NodeRef)actionProps.get(CopyActionExecuter.PARAM_DESTINATION_FOLDER);
         this.actionProperties.put(PROP_DESTINATION, destNodeRef.getId());
      }
      else if (this.action.equals(MailActionExecuter.NAME))
      {
         String subject = (String)actionProps.get(MailActionExecuter.PARAM_SUBJECT);
         this.actionProperties.put(PROP_SUBJECT, subject);
         
         String message = (String)actionProps.get(MailActionExecuter.PARAM_TEXT);
         this.actionProperties.put(PROP_MESSAGE, message);
         
         String to = (String)actionProps.get(MailActionExecuter.PARAM_TO);
         this.actionProperties.put(PROP_TO, to);
      }
   }

   /**
    * @return Returns the summary data for the wizard.
    */
   public String getSummary()
   {
      String summaryCondition = this.ruleService.getConditionDefinition(
            this.condition).getTitle();
      
      String summaryAction = this.ruleService.getActionDefinition(
            this.action).getTitle();
      
      return buildSummary(
            new String[] {"Name", "Description", "Condition", "Action"},
            new String[] {this.title, this.description, summaryCondition, summaryAction});
   }
   
   /**
    * @return Returns the description.
    */
   public String getDescription()
   {
      return description;
   }
   
   /**
    * @param description The description to set.
    */
   public void setDescription(String description)
   {
      this.description = description;
   } 

   /**
    * @return Returns the title.
    */
   public String getTitle()
   {
      return title;
   }
   
   /**
    * @param title The title to set.
    */
   public void setTitle(String title)
   {
      this.title = title;
   }

   /**
    * @return Returns the type.
    */
   public String getType()
   {
      return type;
   }

   /**
    * @param type The type to set
    */
   public void setType(String type)
   {
      this.type = type;
   }
   
   /**
    * @return Returns the selected action
    */
   public String getAction()
   {
      return this.action;
   }

   /**
    * @param action Sets the selected action
    */
   public void setAction(String action)
   {
      this.action = action;
   }

   /**
    * @return Returns the selected condition
    */
   public String getCondition()
   {
      return this.condition;
   }

   /**
    * @param condition Sets the selected condition
    */
   public void setCondition(String condition)
   {
      this.condition = condition;
   }

   /**
    * @param ruleService Sets the rule service to use
    */
   public void setRuleService(RuleService ruleService)
   {
      this.ruleService = ruleService;
   }
   
   /**
    * Sets the RulesBean instance to be used by the wizard in edit mode
    * 
    * @param rulesBean The RulesBean
    */
   public void setRulesBean(RulesBean rulesBean)
   {
      this.rulesBean = rulesBean;
   }

   /**
    * @return Returns the list of selectable actions
    */
   public List<SelectItem> getActions()
   {
      if (this.actions == null)
      {
         List<RuleActionDefinition> ruleActions = this.ruleService.getActionDefinitions();
         this.actions = new ArrayList<SelectItem>();
         for (RuleActionDefinition ruleActionDef : ruleActions)
         {
            this.actions.add(new SelectItem(ruleActionDef.getName(), ruleActionDef.getTitle()));
         }
         
         // make sure the list is sorted by the label
         QuickSort sorter = new QuickSort(this.actions, "label", true, IDataContainer.SORT_CASEINSENSITIVE);
         sorter.sort();
      }
      
      return this.actions;
   }
   
   /**
    * @return Returns a map of all the action descriptions 
    */
   public Map<String, String> getActionDescriptions()
   {
      if (this.actionDescriptions == null)
      {
         List<RuleActionDefinition> ruleActions = this.ruleService.getActionDefinitions();
         this.actionDescriptions = new HashMap<String, String>();
         for (RuleActionDefinition ruleActionDef : ruleActions)
         {
            this.actionDescriptions.put(ruleActionDef.getName(), ruleActionDef.getDescription());
         }
      }
      
      return this.actionDescriptions;
   }

   /**
    * @return Returns the list of selectable conditions
    */
   public List<SelectItem> getConditions()
   {
      if (this.conditions == null)
      {
         List<RuleConditionDefinition> ruleConditions = this.ruleService.getConditionDefinitions();
         this.conditions = new ArrayList<SelectItem>();
         for (RuleConditionDefinition ruleConditionDef : ruleConditions)
         {
            this.conditions.add(new SelectItem(ruleConditionDef.getName(), 
                  ruleConditionDef.getTitle()));
         }
         
         // make sure the list is sorted by the label
         QuickSort sorter = new QuickSort(this.conditions, "label", true, IDataContainer.SORT_CASEINSENSITIVE);
         sorter.sort();
      }
      
      return this.conditions;
   }
   
   /**
    * @return Returns a map of all the condition descriptions 
    */
   public Map<String, String> getConditionDescriptions()
   {
      if (this.conditionDescriptions == null)
      {
         List<RuleConditionDefinition> ruleConditions = this.ruleService.getConditionDefinitions();
         this.conditionDescriptions = new HashMap<String, String>();
         for (RuleConditionDefinition ruleConditionDef : ruleConditions)
         {
            this.conditionDescriptions.put(ruleConditionDef.getName(), 
                  ruleConditionDef.getDescription());
         }
      }
      
      return this.conditionDescriptions;
   }

   /**
    * @return Returns the types of rules that can be defined
    */
   public List<SelectItem> getTypes()
   {
      if (this.types == null)
      {
         List<RuleType> ruleTypes = this.ruleService.getRuleTypes();
         this.types = new ArrayList<SelectItem>();
         for (RuleType ruleType : ruleTypes)
         {
            this.types.add(new SelectItem(ruleType.getName(), ruleType.getDisplayLabel()));
         }
      }
      
      return this.types;
   }

   /**
    * @return Gets the condition settings 
    */
   public Map<String, String> getConditionProperties()
   {
      return this.conditionProperties;
   }
   
   /**
    * @return Gets the action settings
    */
   public Map<String, String> getActionProperties()
   {
      return this.actionProperties;
   }
   
   /**
    * Returns the transformers that are available
    * 
    * @return List of SelectItem objects representing the available transformers
    */
   public List<SelectItem> getTransformers()
   {
      if (this.transformers == null)
      {
         ConfigService svc = (ConfigService)FacesContextUtils.getRequiredWebApplicationContext(
               FacesContext.getCurrentInstance()).getBean(Application.BEAN_CONFIG_SERVICE);
         Config wizardCfg = svc.getConfig("New Rule Wizard");
         if (wizardCfg != null)
         {
            ConfigElement transformersCfg = wizardCfg.getConfigElement("transformers");
            if (transformersCfg != null)
            {               
               this.transformers = new ArrayList<SelectItem>();
               for (ConfigElement child : transformersCfg.getChildren())
               {
                  this.transformers.add(new SelectItem(child.getAttribute("id"), 
                        child.getAttribute("description")));
               }
               
               // make sure the list is sorted by the label
               QuickSort sorter = new QuickSort(this.transformers, "label", true, IDataContainer.SORT_CASEINSENSITIVE);
               sorter.sort();
            }
            else
            {
               logger.warn("Could not find transformers configuration element");
            }
         }
         else
         {
            logger.warn("Could not find New Rule Wizard configuration section");
         }
      }
      
      return this.transformers;
   }
   
   /**
    * Returns the image transformers that are available
    * 
    * @return List of SelectItem objects representing the available image transformers
    */
   public List<SelectItem> getImageTransformers()
   {
      if (this.imageTransformers == null)
      {
         ConfigService svc = (ConfigService)FacesContextUtils.getRequiredWebApplicationContext(
               FacesContext.getCurrentInstance()).getBean(Application.BEAN_CONFIG_SERVICE);
         Config wizardCfg = svc.getConfig("New Rule Wizard");
         if (wizardCfg != null)
         {
            ConfigElement transformersCfg = wizardCfg.getConfigElement("image-transformers");
            if (transformersCfg != null)
            {               
               this.imageTransformers = new ArrayList<SelectItem>();
               for (ConfigElement child : transformersCfg.getChildren())
               {
                  this.imageTransformers.add(new SelectItem(child.getAttribute("id"), 
                        child.getAttribute("description")));
               }
               
               // make sure the list is sorted by the label
               QuickSort sorter = new QuickSort(this.imageTransformers, "label", true, IDataContainer.SORT_CASEINSENSITIVE);
               sorter.sort();
            }
            else
            {
               logger.warn("Could not find image-transformers configuration element");
            }
         }
         else
         {
            logger.warn("Could not find New Rule Wizard configuration section");
         }
      }
      
      return this.imageTransformers;
   }
   
   /**
    * Returns the aspects that are available
    * 
    * @return List of SelectItem objects representing the available aspects
    */
   public List<SelectItem> getAspects()
   {
      if (this.aspects == null)
      {
         ConfigService svc = (ConfigService)FacesContextUtils.getRequiredWebApplicationContext(
               FacesContext.getCurrentInstance()).getBean(Application.BEAN_CONFIG_SERVICE);
         Config wizardCfg = svc.getConfig("New Rule Wizard");
         if (wizardCfg != null)
         {
            ConfigElement aspectsCfg = wizardCfg.getConfigElement("aspects");
            if (aspectsCfg != null)
            {               
               this.aspects = new ArrayList<SelectItem>();
               for (ConfigElement child : aspectsCfg.getChildren())
               {
                  this.aspects.add(new SelectItem(child.getAttribute("id"), 
                        child.getAttribute("description")));
               }
               
               // make sure the list is sorted by the label
               QuickSort sorter = new QuickSort(this.aspects, "label", true, IDataContainer.SORT_CASEINSENSITIVE);
               sorter.sort();
            }
            else
            {
               logger.warn("Could not find aspects configuration element");
            }
         }
         else
         {
            logger.warn("Could not find New Rule Wizard configuration section");
         }
      }
      
      return this.aspects;
   }
   
   public List<SelectItem> getUsers()
   {
      if (this.users == null)
      {
         List<Node> userNodes = Repository.getUsers(FacesContext.getCurrentInstance(),
               this.nodeService);
         this.users = new ArrayList<SelectItem>();
         for (Node user : userNodes)
         {
            String email = (String)user.getProperties().get("email");
            if (email != null && email.length() > 0)
            {
               this.users.add(new SelectItem(email, (String)user.getProperties().get("fullName")));
            }
         }
         
         // make sure the list is sorted by the label
         QuickSort sorter = new QuickSort(this.users, "label", true, IDataContainer.SORT_CASEINSENSITIVE);
         sorter.sort();
      }
      
      return this.users;
   }
   
   /**
    * @see org.alfresco.web.bean.wizard.AbstractWizardBean#determineOutcomeForStep(int)
    */
   protected String determineOutcomeForStep(int step)
   {
      String outcome = null;
      
      switch(step)
      {
         case 1:
         {
            outcome = "details";
            break;
         }
         case 2:
         {
            outcome = "condition";
            break;
         }
         case 3:
         {
            outcome = this.condition;
            break;
         }
         case 4:
         {
            outcome = "action";
            break;
         }
         case 5:
         {
            outcome = this.action;
            break;
         }
         case 6:
         {
            outcome = "summary";
            break;
         }
         default:
         {
            outcome = CANCEL_OUTCOME;
         }
      }
      
      return outcome;
   }   
}
