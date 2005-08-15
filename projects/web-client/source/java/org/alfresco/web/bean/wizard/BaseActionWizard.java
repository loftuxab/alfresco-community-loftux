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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.jsf.FacesContextUtils;

/**
 * Base handler class containing common code used by the New Space Wizard and New Action Wizard
 * 
 * @author gavinc kevinr
 */
public abstract class BaseActionWizard extends AbstractWizardBean
{
   // parameter names for actions
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
   public static final String PROP_TRANSFORM_OPTIONS = "transformOptions";
   public static final String PROP_MESSAGE = "message";
   public static final String PROP_SUBJECT = "subject";
   public static final String PROP_TO = "to";
   
   private static Log logger = LogFactory.getLog(BaseActionWizard.class);
   
   // new rule/action wizard specific properties
   protected String action;
   protected ActionService actionService;
   protected List<SelectItem> actions;
   protected List<SelectItem> transformers;
   protected List<SelectItem> imageTransformers;
   protected List<SelectItem> aspects;
   protected List<SelectItem> users;
   protected Map<String, String> actionDescriptions;
   protected Map<String, String> actionProperties;
   
   
   /**
    * Initialises the wizard
    */
   public void init()
   {
      super.init();
      
      this.action = "add-features";
      
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
      
      if (this.actionDescriptions != null)
      {
         this.actionDescriptions.clear();
         this.actionDescriptions = null;
      }
      
      this.actionProperties = new HashMap<String, String>(3);
      
      // default the approve and reject actions
      this.actionProperties.put("approveAction", "move");
      this.actionProperties.put("rejectStepPresent", "yes");
      this.actionProperties.put("rejectAction", "move");
   }
   
   /**
    * Build the param map for the current Action instance
    * 
    * @return param map
    */
   protected Map<String, Serializable> buildActionParams()
   {
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
      else if (this.action.equals(ImageTransformActionExecuter.NAME))
      {
         // add the transformer to use
         actionParams.put(ImageTransformActionExecuter.PARAM_MIME_TYPE,
               this.actionProperties.get(PROP_IMAGE_TRANSFORMER));
         
         // add the options
         actionParams.put(ImageTransformActionExecuter.PARAM_CONVERT_COMMAND, 
               this.actionProperties.get(PROP_TRANSFORM_OPTIONS));
         
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
      
      return actionParams;
   }
   
   /**
    * Populate the actionProperties member variable with correct props for the current action
    * using the supplied property map.
    * 
    * @param actionProps Map to retrieve props appropriate to the current action from
    */
   protected void populateActionFromProperties(Map<String, Serializable> actionProps)
   {
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
      else if (this.action.equals(ImageTransformActionExecuter.NAME))
      {
         String transformer = (String)actionProps.get(TransformActionExecuter.PARAM_MIME_TYPE);
         this.actionProperties.put(PROP_IMAGE_TRANSFORMER, transformer);
         
         String options = (String)actionProps.get(ImageTransformActionExecuter.PARAM_CONVERT_COMMAND);
         this.actionProperties.put(PROP_TRANSFORM_OPTIONS, options != null ? options : "");
         
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
    * Sets the action service
    * 
    * @param actionRegistration  the action service
    */
   public void setActionService(ActionService actionService)
   {
	  this.actionService = actionService;
   }

   /**
    * @return Returns the list of selectable actions
    */
   public List<SelectItem> getActions()
   {
      if (this.actions == null)
      {
         List<ActionDefinition> ruleActions = this.actionService.getActionDefinitions();
         this.actions = new ArrayList<SelectItem>();
         for (ActionDefinition ruleActionDef : ruleActions)
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
         List<ActionDefinition> ruleActions = this.actionService.getActionDefinitions();
         this.actionDescriptions = new HashMap<String, String>();
         for (ActionDefinition ruleActionDef : ruleActions)
         {
            this.actionDescriptions.put(ruleActionDef.getName(), ruleActionDef.getDescription());
         }
      }
      
      return this.actionDescriptions;
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
         Config wizardCfg = svc.getConfig("Action Wizards");
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
            logger.warn("Could not find Action Wizards configuration section");
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
         Config wizardCfg = svc.getConfig("Action Wizards");
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
            logger.warn("Could not find Action Wizards configuration section");
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
         Config wizardCfg = svc.getConfig("Action Wizards");
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
            logger.warn("Could not find Action Wizards configuration section");
         }
      }
      
      return this.aspects;
   }
   
   /**
    * @return the List of users in the system wrapped in SelectItem objects
    */
   public List<SelectItem> getUsers()
   {
      if (this.users == null)
      {
         List<Node> userNodes = Repository.getUsers(
               FacesContext.getCurrentInstance(),
               this.nodeService,
               this.searchService);
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
}
