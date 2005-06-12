package org.alfresco.web.bean.wizard;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.transaction.UserTransaction;

import org.alfresco.config.Config;
import org.alfresco.config.ConfigElement;
import org.alfresco.config.ConfigService;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.repo.dictionary.NamespaceService;
import org.alfresco.repo.dictionary.impl.DictionaryBootstrap;
import org.alfresco.repo.ref.QName;
import org.alfresco.repo.rule.Rule;
import org.alfresco.repo.rule.RuleActionDefinition;
import org.alfresco.repo.rule.RuleConditionDefinition;
import org.alfresco.repo.rule.RuleService;
import org.alfresco.repo.rule.RuleType;
import org.alfresco.repo.rule.impl.condition.MatchTextEvaluator;
import org.alfresco.web.app.Application;
import org.alfresco.web.bean.repository.Node;
import org.alfresco.web.bean.repository.Repository;
import org.apache.log4j.Logger;
import org.springframework.web.jsf.FacesContextUtils;

/**
 * Handler class used by the New Space Wizard 
 * 
 * @author gavinc
 */
public class NewRuleWizard extends AbstractWizardBean
{
   private static Logger logger = Logger.getLogger(NewRuleWizard.class);
   
   // TODO: retrieve these from the config service
   private static final String WIZARD_TITLE = "New Rule Wizard";
   private static final String WIZARD_DESC = "Use this wizard to create a new rule.";
   private static final String STEP1_TITLE = "Step One - Enter Details";
   private static final String STEP2_TITLE = "Step Two - Select Condition";
   private static final String STEP3_TITLE = "Step Three - Condition Settings";
   private static final String STEP4_TITLE = "Step Four - Select Action";
   private static final String STEP5_TITLE = "Step Five - Action Settings";
   private static final String FINISH_INSTRUCTION = "To create the rule click Finish.";
   
   // new rule wizard specific properties
   private String title;
   private String description;
   private String type;
   private String condition;
   private String action;
   private RuleService ruleService;
   private List<SelectItem> types;
   private List<SelectItem> conditions;
   private List<SelectItem> actions;
   private List<SelectItem> transformers;
   private List<SelectItem> features;
   private Map<String, String> conditionDescriptions;
   private Map<String, String> actionDescriptions;
   private Map<String, String> conditionProperties;
   private Map<String, String> actionProperties;
   
   // condition and action specific lists - TEMP, picker components will be used
   private List<SelectItem> categories;
   
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
         
         if (this.editMode)
         {
            // update the existing rule in the repository
         }
         else
         {
            // get hold of the space the rule will apply to and make sure
            // it is actionable
            Node currentSpace = browseBean.getActionSpace();
            if (this.ruleService.isActionable(currentSpace.getNodeRef()) == false)
            {
               this.ruleService.makeActionable(currentSpace.getNodeRef());
            }
        
            RuleType ruleType = this.ruleService.getRuleType(this.getType());
            RuleConditionDefinition cond = this.ruleService.getConditionDefintion(this.getCondition());
            RuleActionDefinition action = this.ruleService.getActionDefinition(this.getAction());
        
            // set up parameters maps for the condition and acion
            Map<String, Serializable> conditionParams = new HashMap<String, Serializable>();
            if (this.condition.equals("match-text"))
            {
               conditionParams.put(MatchTextEvaluator.PARAM_TEXT, 
                     this.conditionProperties.get("containstext"));
            }
            else if (this.condition.equals("in-category"))
            {
               // add category id to the condition params map
            }
            
            Map<String, Serializable> actionParams = new HashMap<String, Serializable>();
            if (this.action.equals("add-features"))
            {
               // create QName representation of the chosen feature
               // TODO: handle namespaces, for now presume it is in alfresco namespace
               QName aspect = QName.createQName(NamespaceService.ALFRESCO_URI, 
                     this.actionProperties.get("feature"));
               
               actionParams.put("aspect-name", aspect);
            }
            else if (this.action.equals("copy") || this.action.equals("move"))
            {
               // add the destination space id to the action properties
            }
            
            // create the rule and add it to the space
            Rule rule = this.ruleService.createRule(ruleType);
            rule.setTitle(this.title);
            rule.setDescription(this.description);
            rule.addRuleCondition(cond, conditionParams);
            rule.addRuleAction(action, actionParams);
            this.ruleService.addRule(currentSpace.getNodeRef(), rule);
            
            if (logger.isDebugEnabled())
               logger.debug("Added rule '" + this.title + "' with condition '" + 
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
      String stepDesc = null;
      
      switch (this.currentStep)
      {
         case 6:
         {
            stepDesc = SUMMARY_DESCRIPTION;
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
            stepInstruction = FINISH_INSTRUCTION;
            break;
         }
         case 6:
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
      
      this.title = null;
      this.description = null;
      this.type = "inbound";
      this.action = "add-features";
      this.condition = "no-condition";
      
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
   }
   
   /**
    * @return Returns the summary data for the wizard.
    */
   public String getSummary()
   {
      return buildSummary(
            new String[] {"Name", "Description", "Condition", "Action"},
            new String[] {this.title, this.description, this.condition, this.action});
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
   
   // TEMP - find out from Andy how you get hold of categories - if you can!
   public List<SelectItem> getCategories()
   {
      if (this.categories == null)
      {
         this.categories = new ArrayList<SelectItem>();
         this.categories.add(new SelectItem("category1", "Category 1"));
         this.categories.add(new SelectItem("category2", "Category 2"));
         this.categories.add(new SelectItem("category3", "Category 3"));
      }
      
      return this.categories;
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
    * Returns the features that are available
    * 
    * @return List of SelectItem objects representing the available features
    */
   public List<SelectItem> getFeatures()
   {
      if (this.features == null)
      {
         ConfigService svc = (ConfigService)FacesContextUtils.getRequiredWebApplicationContext(
               FacesContext.getCurrentInstance()).getBean(Application.BEAN_CONFIG_SERVICE);
         Config wizardCfg = svc.getConfig("New Rule Wizard");
         if (wizardCfg != null)
         {
            ConfigElement featuresCfg = wizardCfg.getConfigElement("features");
            if (featuresCfg != null)
            {               
               this.features = new ArrayList<SelectItem>();
               for (ConfigElement child : featuresCfg.getChildren())
               {
                  this.features.add(new SelectItem(child.getAttribute("id"), 
                        child.getAttribute("description")));
               }
            }
            else
            {
               logger.warn("Could not find features configuration element");
            }
         }
         else
         {
            logger.warn("Could not find New Rule Wizard configuration section");
         }
      }
      
      return this.features;
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
