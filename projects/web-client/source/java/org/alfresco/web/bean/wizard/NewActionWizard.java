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
import java.text.MessageFormat;
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
import org.alfresco.repo.action.evaluator.InCategoryEvaluator;
import org.alfresco.repo.action.evaluator.MatchTextEvaluator;
import org.alfresco.repo.action.executer.AddFeaturesActionExecuter;
import org.alfresco.repo.action.executer.CheckInActionExecuter;
import org.alfresco.repo.action.executer.CheckOutActionExecuter;
import org.alfresco.repo.action.executer.CopyActionExecuter;
import org.alfresco.repo.action.executer.ImageTransformActionExecuter;
import org.alfresco.repo.action.executer.LinkCategoryActionExecuter;
import org.alfresco.repo.action.executer.MailActionExecuter;
import org.alfresco.repo.action.executer.MoveActionExecuter;
import org.alfresco.repo.action.executer.SimpleWorkflowActionExecuter;
import org.alfresco.repo.action.executer.TransformActionExecuter;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ActionConditionDefinition;
import org.alfresco.service.cmr.action.ActionDefinition;
import org.alfresco.service.cmr.action.ActionService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.rule.Rule;
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
import org.alfresco.web.ui.common.Utils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.jsf.FacesContextUtils;

/**
 * Handler class used by the New Action Wizard 
 * 
 * @author Kevin Roast
 */
public class NewActionWizard extends BaseActionWizard
{
   private static Log logger = LogFactory.getLog(NewActionWizard.class);
   
   private static final String ERROR = "Failed to create Action due to error: {0}";
   
   // TODO: retrieve these from the config service
   private static final String WIZARD_TITLE = "Custom Action Wizard";
   private static final String WIZARD_DESC = "This wizard helps you create a custom action for";
   private static final String STEP1_TITLE = "Step One - Select Action";
   private static final String STEP2_TITLE = "Step Two - Action Settings";
   private static final String FINISH_INSTRUCTION = "To execute the action click Finish.";
   
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
         tx = Repository.getUserTransaction(FacesContext.getCurrentInstance());
         tx.begin();
         
         // build the action params map based on the selected action instance
         Map<String, Serializable> actionParams = buildActionParams();
         
         // build the action to execute
         Action action = this.actionService.createAction(getAction());
         action.setParameterValues(actionParams);
         
         // execute the action on the current document node
         this.actionService.executeAction(action, this.browseBean.getDocument().getNodeRef());
         
         if (logger.isDebugEnabled())
         {
            logger.debug("Executed action '" + this.action + 
                         "' with action params of " + 
                         this.actionProperties);
         }
         
         // reset the current document properties/aspects in case we have changed them
         // during the execution of the custom action
         this.browseBean.getDocument().reset();
         
         // commit the transaction
         tx.commit();
      }
      catch (Exception e)
      {
         // rollback the transaction
         try { if (tx != null) {tx.rollback();} } catch (Exception ex) {}
         Utils.addErrorMessage( MessageFormat.format(ERROR, e.getMessage()), e);
         outcome = null;
      }
      
      return outcome;
   }

   /**
    * @see org.alfresco.web.bean.wizard.AbstractWizardBean#getWizardDescription()
    */
   public String getWizardDescription()
   {
      return WIZARD_DESC;
   }

   /**
    * @see org.alfresco.web.bean.wizard.AbstractWizardBean#getWizardTitle()
    */
   public String getWizardTitle()
   {
      return WIZARD_TITLE;
   }
   
   /**
    * @see org.alfresco.web.bean.wizard.AbstractWizardBean#getStepDescription()
    */
   public String getStepDescription()
   {
      return "";
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
         case 2:
         case 3:
         {
            stepInstruction = FINISH_INSTRUCTION;
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
   }

   /**
    * @return Returns the summary data for the wizard.
    */
   public String getSummary()
   {
      String summaryAction = this.actionService.getActionDefinition(
            this.action).getTitle();
      
      return buildSummary(
            new String[] {"Action"},
            new String[] {summaryAction});
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
            outcome = "action";
            break;
         }
         case 2:
         {
            outcome = this.action;
            break;
         }
         case 3:
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
