/**
 * 
 */
package org.alfresco.repo.rule.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.config.Config;
import org.alfresco.config.ConfigElement;
import org.alfresco.config.ConfigLookupContext;
import org.alfresco.config.ConfigService;
import org.alfresco.repo.rule.ParameterDefinition;
import org.alfresco.repo.rule.ParameterType;
import org.alfresco.repo.rule.RuleServiceException;

/**
 * @author Roy Wetherall
 */
/*package*/ class RuleConfig
{
    /**
     * The config service
     */
    private ConfigService configService;
    
    /**
     * 
     */
    private Map<String, RuleTypeImpl> ruleTypes = new HashMap<String, RuleTypeImpl>();
    private Map<String, RuleActionDefinitionImpl> actionDefinitions = new HashMap<String, RuleActionDefinitionImpl>();
    private Map<String, RuleConditionDefinitionImpl> conditionDefinitions = new HashMap<String, RuleConditionDefinitionImpl>();
    
    /**
     * Constants used in rule config
     */
    private static final String RULE_CONFIG_AREA = "rule_config";
    private static final String RULE_TYPES_CONFIG = "Rule Types";
    private static final String CE_RULE_TYPES = "rule-types";
    private static final String CA_NAME = "name";
    private static final String CE_DISPLAY_LABEL = "display-label";
    private static final String CE_ADAPTER = "adapter";
    private static final String ACTION_DEFINITIONS_CONFIG = "Action Definitions";
    private static final String CE_ACTION_DEFINITIONS = "action-definitions";
    private static final String CONDITION_DEFINITIONS_CONFIG = "Condition Definitions";
    private static final String CE_CONDITION_DEFINITIONS = "condition-definitions";
    private static final String CE_TITLE = "title";
    private static final String CE_DESCRIPTION = "description";
    private static final String CE_PARAMETERS = "parameters";
    private static final String CE_PARAMETER = "parameter";
    private static final String CA_TYPE = "type";
    private static final String CA_DISPLAY_LABEL = "display-label";
    private static final String CE_EVALUTOR = "evaluator";    
    private static final String CE_EXECUTOR = "executor";

    /**
     * Error messages
     */
    private static final String ERR_NO_NAME = "Invalid rule config.  The name attribtue is mandatory for all rule types, actions and conditions.";
    private static final String ERR_UNIQUENESS = "Invalid rule config.  The name {0} is not unique.";
        
    /**
     * Constructor
     * 
     * @param configService     the config service
     */
    public RuleConfig(ConfigService configService)
    {
        this.configService = configService; 
        
        initRuleTypes();
        initActionDefinitions();
        initConditionDefinitions();
    }    
    
    /**
     * Get a list of all the rule types
     * 
     * @return  the rule types
     */
    public Collection<RuleTypeImpl> getRuleTypes()
    {
        return this.ruleTypes.values();
    }
    
    /**
     * Gets a rule type by name
     * 
     * @param name  the rule type name
     * @return      the rule type 
     */
    public RuleTypeImpl getRuleType(String name)
    {
        return this.ruleTypes.get(name);
    }

    /**
     * 
     * @return
     */
    public Collection<RuleConditionDefinitionImpl> getConditionDefinitions()
    {
        return this.conditionDefinitions.values();
    }
    
    /**
     * 
     * @param name
     * @return
     */
    public RuleConditionDefinitionImpl getConditionDefinition(String name)
    {
        return this.conditionDefinitions.get(name);
    }

    /**
     * 
     * @return
     */
    public Collection<RuleActionDefinitionImpl> getActionDefinitions()
    {
        return this.actionDefinitions.values();
    }
    
    /**
     * 
     * @param name
     * @return
     */
    public RuleActionDefinitionImpl getActionDefinition(String name)
    {
        return this.actionDefinitions.get(name);
    }
    
    /**
     * Initialise the rule types from the repo config.
     */
    private void initRuleTypes()
    {
        Config ruleTypesConfig = this.configService.getConfig(RULE_TYPES_CONFIG, 
              new ConfigLookupContext(RULE_CONFIG_AREA));
        ConfigElement configElement = ruleTypesConfig.getConfigElement(CE_RULE_TYPES);
        for (ConfigElement ruleTypeConfig : configElement.getChildren())
        {
            // Get the name of the rule type
            String name = getNameFromConfig(ruleTypeConfig, this.ruleTypes.keySet());
            
            // Create the rule type data object
            RuleTypeImpl ruleTypeData = new RuleTypeImpl(name);
            
            // Get the rule type display label
            for (ConfigElement childConfig : ruleTypeConfig.getChildren())
            {   
                String configElementName = childConfig.getName();
                if (CE_DISPLAY_LABEL.equals(configElementName) == true)
                {
                    ruleTypeData.setDisplayLabel(childConfig.getValue());
                }
                else if (CE_ADAPTER.equals(configElementName) == true)
                {
                    ruleTypeData.setRuleTypeAdapter(childConfig.getValue());
                }
            }
                                  
            
            // Add the rule type info to the cache
            this.ruleTypes.put(name, ruleTypeData);
        }
    }
    
    /**
     * Get the name attribute from a config element and check it is valid and unique.
     * 
     * @param configElement     the config element
     * @param keys              the key set within which to check for uniqueness
     * @return                  the name
     */
    private String getNameFromConfig(ConfigElement configElement, Set<String> keys)
    {
        // Get the name of the rule type
        String name = configElement.getAttribute(CA_NAME);
        if (name == null)
        {
            throw new RuleServiceException(ERR_NO_NAME);
        }
        
        // Check the uniqueness of the rule type name
        if (keys.contains(name) == true)
        {
            throw new RuleServiceException(
                    MessageFormat.format(ERR_UNIQUENESS, new Object[]{name}));
        }
        
        return name;
    }
    
    /**
     * 
     */
    private void initActionDefinitions()
    {
        Config config = this.configService.getConfig(ACTION_DEFINITIONS_CONFIG, 
              new ConfigLookupContext(RULE_CONFIG_AREA));
        ConfigElement configElement = config.getConfigElement(CE_ACTION_DEFINITIONS);
        for (ConfigElement ruleItemConfig : configElement.getChildren())
        {
            // Get the name 
            String name = getNameFromConfig(ruleItemConfig, this.actionDefinitions.keySet());

            // Create the data item
            RuleActionDefinitionImpl actionDefintionData = new RuleActionDefinitionImpl(name);
            initItemDefintion(actionDefintionData, ruleItemConfig);
            
            // Get the executor string
            for (ConfigElement childConfig : ruleItemConfig.getChildren())
            {   
                String configElementName = childConfig.getName();
                if (CE_EXECUTOR.equals(configElementName) == true)
                {
                    actionDefintionData.setRuleActionExecutor(childConfig.getValue());
                }
            }
            
            // Add the item to the cache
            this.actionDefinitions.put(name, actionDefintionData);
        }
    }
    
    /**
     * 
     */
    private void initConditionDefinitions()
    {
        Config config = this.configService.getConfig(CONDITION_DEFINITIONS_CONFIG, 
              new ConfigLookupContext(RULE_CONFIG_AREA));
        ConfigElement configElement = config.getConfigElement(CE_CONDITION_DEFINITIONS);
        for (ConfigElement ruleItemConfig : configElement.getChildren())
        {
            // Get the name 
            String name = getNameFromConfig(ruleItemConfig, this.conditionDefinitions.keySet());            

            // Create the data item
            RuleConditionDefinitionImpl conditionDefintionData = new RuleConditionDefinitionImpl(name);
            initItemDefintion(conditionDefintionData, ruleItemConfig);
            
            // Get the evaluator string
            for (ConfigElement childConfig : ruleItemConfig.getChildren())
            {   
                String configElementName = childConfig.getName();
                if (CE_EVALUTOR.equals(configElementName) == true)
                {
                    conditionDefintionData.setConditionEvaluator(childConfig.getValue());
                }
            }
                       
            // Add the item to the cache
            this.conditionDefinitions.put(name, conditionDefintionData);

        }
    }
    
    /**
     * 
     * @param itemDefinitionData
     * @param ruleItemConfig
     */
    private void initItemDefintion(RuleItemDefinitionImpl itemDefinitionData, ConfigElement ruleItemConfig)
    {
        // Get the details of the rule item
        for (ConfigElement childConfig : ruleItemConfig.getChildren())
        {   
            String configElementName = childConfig.getName();
            if (CE_TITLE.equals(configElementName) == true)
            {
                itemDefinitionData.setTitle(childConfig.getValue());
            }
            else if (CE_DESCRIPTION.equals(configElementName) == true)
            {
                itemDefinitionData.setDescription(childConfig.getValue());
            }
            else if (CE_PARAMETERS.equals(configElementName) == true)
            {
                for (ConfigElement propertyConfig : childConfig.getChildren())
                {
                    List<ParameterDefinition> paramDefs = new ArrayList<ParameterDefinition>();
                    
                    if(CE_PARAMETER.equals(propertyConfig.getName()) == true)
                    {
                        String name = propertyConfig.getAttribute(CA_NAME);
                        String type = propertyConfig.getAttribute(CA_TYPE);
                        String displayLabel = propertyConfig.getAttribute(CA_DISPLAY_LABEL);
                        paramDefs.add(
                                new ParameterDefinitionImpl(
                                        name, 
                                        ParameterType.valueOf(type),
                                        displayLabel));
                        
                    }
                    
                    itemDefinitionData.setParameterDefinitions(paramDefs);
                }
            }
        }
    }
}
