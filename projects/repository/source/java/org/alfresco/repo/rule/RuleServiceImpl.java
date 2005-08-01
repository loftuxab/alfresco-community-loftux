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
package org.alfresco.repo.rule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.rule.action.RuleActionExecuter;
import org.alfresco.repo.rule.common.RuleActionDefinitionImpl;
import org.alfresco.repo.rule.common.RuleConditionDefinitionImpl;
import org.alfresco.repo.rule.common.RuleImpl;
import org.alfresco.repo.rule.condition.RuleConditionEvaluator;
import org.alfresco.repo.rule.ruletype.RuleTypeAdapter;
import org.alfresco.repo.transaction.AlfrescoTransactionSupport;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.configuration.ConfigurableService;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.rule.ParameterDefinition;
import org.alfresco.service.cmr.rule.ParameterType;
import org.alfresco.service.cmr.rule.Rule;
import org.alfresco.service.cmr.rule.RuleAction;
import org.alfresco.service.cmr.rule.RuleActionDefinition;
import org.alfresco.service.cmr.rule.RuleCondition;
import org.alfresco.service.cmr.rule.RuleConditionDefinition;
import org.alfresco.service.cmr.rule.RuleService;
import org.alfresco.service.cmr.rule.RuleServiceException;
import org.alfresco.service.cmr.rule.RuleType;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.GUID;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Rule service implementation.
 * <p>
 * This service automatically binds to the transaction flush hooks.  It will
 * therefore participate in any flushes that occur during the transaction as
 * well.
 * 
 * @author Roy Wetherall   
 */
public class RuleServiceImpl implements RuleService, ApplicationContextAware
{
    /** key against which to store rules pending on the current transaction */
    private static final String KEY_RULES_PENDING = "RuleServiceImpl.PendingRules";
    /** key against which to store executed rules on the current transaction */
    private static final String KEY_RULES_EXECUTED = "RuleServiceImpl.ExecutedRules";
    
	/**
	 * The application context
	 */
	private ApplicationContext applicationContext;
    
    /**
     * The node service
     */
    private NodeService nodeService;
    
    /**
     * The content service
     */
    private ContentService contentService;
    
    /**
     * The dictionary service
     */
    private DictionaryService dictionaryService;
	
	/**
	 * The service registry
	 */
	private ServiceRegistry serviceRegistry;
    
    /**
     * The policy component
     */
    private PolicyComponent policyComponent;
    
    private ConfigurableService configService;
    
    /**
     * The rule store
     */
    private RuleStore ruleStore;
    
//	/**
//	 * Thread local set containing all the pending rules to be executed when the transaction ends
//	 */
//	private ThreadLocal<Set<PendingRuleData>> threadLocalPendingData = new ThreadLocal<Set<PendingRuleData>>();
//	
//	/**
//	 * Thread local set to the currently executing rule (this should prevent and execution loops)
//	 */
//	private ThreadLocal<Set<ExecutedRuleData>> threadLocalExecutedRules = new ThreadLocal<Set<ExecutedRuleData>>();
//    
    /**
     * List of disabled node refs.  The rules associated with these nodes will node be added to the pending list, and
     * therefore not fired.  This list is transient.
     */
    private Set<NodeRef> disabledNodeRefs = new HashSet<NodeRef>(5);

	/**
	 * All the rule type currently registered
	 */
	private Map<String, RuleType> ruleTypes = new HashMap<String, RuleType>();
	
	/**
	 * All the condition definitions currently registered
	 */
	private Map<String, RuleConditionDefinition> conditionDefinitions = new HashMap<String, RuleConditionDefinition>();
	
	/**
	 * All the action definitions currently registered
	 */
	private Map<String, RuleActionDefinition> actionDefinitions = new HashMap<String, RuleActionDefinition>();       

    /**
     * Service initialisation methods     
     */
    private void init()
    {
        // Register the copy policy behaviour
        this.policyComponent.bindClassBehaviour(
                QName.createQName(NamespaceService.ALFRESCO_URI, "onCopyComplete"),
                ContentModel.ASPECT_ACTIONABLE,
                new JavaBehaviour(this, "onCopyComplete"));
    }
    
	/**
	 * Set the application context
	 * 
	 * @param applicationContext	the application context
	 */
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException 
	{
		this.applicationContext = applicationContext;
	}
	
	/**
	 * Set the rule store
	 * 
	 * @param ruleStore		the rule store
	 */
	public void setRuleStore(RuleStore ruleStore) 
	{
		this.ruleStore = ruleStore;
		((RuleStoreImpl)this.ruleStore).setRuleService(this);
	}
    
    /**
     * Set the node service 
     * 
     * @param nodeService   the node service
     */
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }
    
    /**
     * Set the content service
     * 
     * @param contentService    the content service
     */
    public void setContentService(ContentService contentService)
    {
        this.contentService = contentService;
    }
    
    /**
     * Set the dictionary service
     * 
     * @param dictionaryService     the dictionary service
     */
    public void setDictionaryService(DictionaryService dictionaryService)
    {
        this.dictionaryService = dictionaryService;
    }
	
	/**
	 * Set the service registry
	 * 
	 * @param serviceRegistry	the service registry
	 */
	public void setServiceRegistry(ServiceRegistry serviceRegistry) 
	{
		this.serviceRegistry = serviceRegistry;
	}
    
    /**
     * Sets the policy component
     * 
     * @param policyComponent  the policy component
     */
    public void setPolicyComponent(PolicyComponent policyComponent)
    {
        this.policyComponent = policyComponent;
    }
    
    /**
     * @see org.alfresco.repo.rule.RuleService#getRuleTypes()
     */
    public List<RuleType> getRuleTypes()
    {
		return new ArrayList<RuleType>(this.ruleTypes.values());
    }
    
    /**
     * @see org.alfresco.repo.rule.RuleService#getRuleType(java.lang.String)
     */
    public RuleType getRuleType(String name)
    {
        return this.ruleTypes.get(name);
    }
    
    public void setConfigService(ConfigurableService configService)
	{
		this.configService = configService;
	}

    /**
     * @see org.alfresco.repo.rule.RuleService#getConditionDefinitions()
     */
    public List<RuleConditionDefinition> getConditionDefinitions()
    {
		return new ArrayList<RuleConditionDefinition>(this.conditionDefinitions.values());
    }
    
    /**
     * @see org.alfresco.repo.rule.RuleService#getConditionDefinition(java.lang.String)
     */
    public RuleConditionDefinition getConditionDefinition(String name)
    {
		return this.conditionDefinitions.get(name);
    }

    /**
     * @see org.alfresco.repo.rule.RuleService#getActionDefinitions()
     */
    public List<RuleActionDefinition> getActionDefinitions()
    {
		return new ArrayList<RuleActionDefinition>(this.actionDefinitions.values());
    }

    /**
     * @see org.alfresco.repo.rule.RuleService#getActionDefinition(java.lang.String)
     */
    public RuleActionDefinition getActionDefinition(String name)
    {
		return this.actionDefinitions.get(name);
    }

    /**
     * @see org.alfresco.repo.rule.RuleService#makeActionable(org.alfresco.repo.ref.NodeRef, org.alfresco.repo.ref.NodeRef)
     */
    public void makeActionable(
            NodeRef nodeRef)
    {
        // Make the node configurable
    	if (this.configService.isConfigurable(nodeRef) == false)
    	{
    		this.configService.makeConfigurable(nodeRef);
    	}
    	
    	// Apply the actionable aspect
    	this.nodeService.addAspect(nodeRef, ContentModel.ASPECT_ACTIONABLE, null);
    }

    /**
     * @see org.alfresco.repo.rule.RuleService#isActionable(org.alfresco.repo.ref.NodeRef)
     */
    public boolean isActionable(NodeRef nodeRef)
    {
        // Determine whether a node is actionable or not
        return (this.nodeService.hasAspect(nodeRef, ContentModel.ASPECT_ACTIONABLE) == true);          
    }
    
    /**
     * @see org.alfresco.service.cmr.rule.RuleService#hasRules(org.alfresco.repo.ref.NodeRef)
     */
    public boolean hasRules(NodeRef nodeRef)
    {
        return this.ruleStore.hasRules(nodeRef);
    }       
    
    /**
     * @see org.alfresco.service.cmr.rule.RuleService#rulesEnabled(NodeRef)
     */
    public boolean rulesEnabled(NodeRef nodeRef)
    {
        return (this.disabledNodeRefs.contains(nodeRef) == false);
    }

    /**
     * @see org.alfresco.service.cmr.rule.RuleService#disableRules(NodeRef)
     */
    public void disableRules(NodeRef nodeRef)
    {
        // Add the node to the set of disabled nodes
        this.disabledNodeRefs.add(nodeRef);
    }

    /**
     * @see org.alfresco.service.cmr.rule.RuleService#enableRules(NodeRef)
     */
    public void enableRules(NodeRef nodeRef)
    {
        // Remove the node from the set of disabled nodes
        this.disabledNodeRefs.remove(nodeRef);
    }

    /**
     * @see org.alfresco.repo.rule.RuleService#getRules(org.alfresco.repo.ref.NodeRef)
     */
    public List<Rule> getRules(NodeRef nodeRef)
    {
        return getRules(nodeRef, true);
    }

    /**  
     * @see org.alfresco.repo.rule.RuleService#getRules(org.alfresco.repo.ref.NodeRef, boolean)
     */
    @SuppressWarnings("unchecked")
    public List<Rule> getRules(NodeRef nodeRef, boolean includeInhertied)
    {
        return (List<Rule>)this.ruleStore.get(nodeRef, includeInhertied);
    }
	
	/**
	 * @see org.alfresco.repo.rule.RuleService#getRule(String) 
	 */
	public Rule getRule(NodeRef nodeRef, String ruleId) 
	{
		return this.ruleStore.getById(nodeRef, ruleId);
	}

    /**
     * @see org.alfresco.repo.rule.RuleService#getRulesByRuleType(org.alfresco.repo.ref.NodeRef, org.alfresco.repo.rule.RuleType)
     */
    @SuppressWarnings("unchecked")
    public List<Rule> getRulesByRuleType(NodeRef nodeRef, RuleType ruleType)
    {
        return (List<Rule>)this.ruleStore.getByRuleType(nodeRef, ruleType);
    }

    /**
     * @see org.alfresco.repo.rule.RuleService#createRule(org.alfresco.repo.rule.RuleType)
     */
    public Rule createRule(RuleType ruleType)
    {
        // Create the new rule, giving it a unique rule id
        String id = GUID.generate();
        return new RuleImpl(id, ruleType);
    }

    /**
     * @see org.alfresco.repo.rule.RuleService#addRule(org.alfresco.repo.ref.NodeRef, org.alfresco.repo.rule.Rule)
     */
    public void addRule(NodeRef nodeRef, Rule rule)
    {
        // Add the rule to the rule store
        this.ruleStore.put(nodeRef, (RuleImpl)rule);
    }
    
    /**
     * @see org.alfresco.repo.rule.RuleService#removeRule(org.alfresco.repo.ref.NodeRef, org.alfresco.repo.rule.common.RuleImpl)
     */
    public void removeRule(NodeRef nodeRef, Rule rule)
    {
        // Remove the rule from the rule store
        this.ruleStore.remove(nodeRef, (RuleImpl)rule);
    }	
    
    /**
     * @see org.alfresco.repo.rule.RuleService#removeAllRules(NodeRef)
     */
    public void removeAllRules(NodeRef nodeRef)
    {
        List<? extends Rule> rules = this.ruleStore.get(nodeRef, false);
        for (Rule rule : rules)
        {
            this.ruleStore.remove(nodeRef, (RuleImpl)rule);
        }
    }
	
	/**
	 * @see org.alfresco.repo.rule.RuleService#addRulePendingExecution(NodeRef, NodeRef, Rule)
	 */
	@SuppressWarnings("unchecked")
    public void addRulePendingExecution(NodeRef actionableNodeRef, NodeRef actionedUponNodeRef, Rule rule) 
	{
        // First check to seee if the node has been disabled
        if (this.disabledNodeRefs.contains(actionableNodeRef) == false)
        {
    		PendingRuleData pendingRuleData = new PendingRuleData(actionableNodeRef, actionedUponNodeRef, rule);
            Set<ExecutedRuleData> executedRules =
                    (Set<ExecutedRuleData>) AlfrescoTransactionSupport.getResource(KEY_RULES_EXECUTED);
    		
    		if (executedRules == null || executedRules.contains(new ExecutedRuleData(actionableNodeRef, rule)) == false)
    		{
                Set<PendingRuleData> pendingRules =
                    (Set<PendingRuleData>) AlfrescoTransactionSupport.getResource(KEY_RULES_PENDING);
    			if (pendingRules == null)
    			{
                    // bind pending rules to the current transaction
    				pendingRules = new HashSet<PendingRuleData>();
                    AlfrescoTransactionSupport.bindResource(KEY_RULES_PENDING, pendingRules);
                    // bind this RuleService to receive callbacks when the transaction flushes
                    AlfrescoTransactionSupport.bindRuleService(this);
    			}
    			
    			pendingRules.add(pendingRuleData);		
    		}
        }
	}

	/**
	 * @see org.alfresco.repo.rule.RuleService#executePendingRules()
	 */
	public void executePendingRules() 
	{
        AlfrescoTransactionSupport.bindResource(KEY_RULES_EXECUTED, new HashSet<ExecutedRuleData>());
        try
        {
            executePendingRulesImpl();
        }
        finally
        {
            AlfrescoTransactionSupport.unbindResource(KEY_RULES_EXECUTED);
        }
	}     
    
    /**
     * Executes the pending rules, iterating until all pending rules have been executed
     */
    @SuppressWarnings("unchecked")
    private void executePendingRulesImpl()
    {
        // get the transaction-local rules to execute
        Set<PendingRuleData> pendingRules =
                (Set<PendingRuleData>) AlfrescoTransactionSupport.getResource(KEY_RULES_PENDING);
        // only execute if there are rules present
        if (pendingRules != null && !pendingRules.isEmpty())
        {
            PendingRuleData[] pendingRulesArr = pendingRules.toArray(new PendingRuleData[0]);
            // remove all pending rules from the transaction
            AlfrescoTransactionSupport.unbindResource(KEY_RULES_PENDING);
            // execute each rule
            for (PendingRuleData pendingRule : pendingRulesArr) 
            {
                executePendingRule(pendingRule);
            }
            
            // Run any rules that have been marked as pending during execution
            executePendingRulesImpl();
        }   
    }
	
	/**
	 * Executes a pending rule
	 * 
	 * @param pendingRule	the pending rule data object
	 */
	@SuppressWarnings("unchecked")
    private void executePendingRule(PendingRuleData pendingRule) 
	{
		NodeRef actionableNodeRef = pendingRule.getActionableNodeRef();
		NodeRef actionedUponNodeRef = pendingRule.getActionedUponNodeRef();
		Rule rule = pendingRule.getRule();
		
		// Get the rule conditions
		List<RuleCondition> conds = rule.getRuleConditions();				
		if (conds.size() == 0)
		{
			throw new RuleServiceException("No rule conditions have been specified for the rule:" + rule.getTitle());
		}
		else if (conds.size() > 1)
		{
			// TODO at the moment we only support one rule condition
			throw new RuleServiceException("Currently only one rule condition can be specified per rule:" + rule.getTitle());
		}
		
		// Get the single rule condition
		RuleCondition cond = conds.get(0);
	    RuleConditionEvaluator evaluator = getConditionEvaluator(cond);
	      
	    // Get the rule acitons
	    List<RuleAction> actions = rule.getRuleActions();
		if (actions.size() == 0)
		{
			throw new RuleServiceException("No rule actions have been specified for the rule:" + rule.getTitle());
		}
		else if (actions.size() > 1)
		{
			// TODO at the moment we only support one rule action
			throw new RuleServiceException("Currently only one rule action can be specified per rule:" + rule.getTitle());
		}
			
		// Get the single action
	    RuleAction action = actions.get(0);
	    RuleActionExecuter executor = getActionExecutor(action);
	      
		// Evaluate the condition
	    if (evaluator.evaluate(cond, actionableNodeRef, actionedUponNodeRef) == true)
	    {
            // Add the rule to the executed rule list
            // (do this before this is executed to prevent rules being added to the pending list) 
            Set<ExecutedRuleData> executedRules =
                    (Set<ExecutedRuleData>) AlfrescoTransactionSupport.getResource(KEY_RULES_EXECUTED);
            executedRules.add(new ExecutedRuleData(actionableNodeRef, rule));
            
			// Execute the rule
	        executor.execute(action, actionableNodeRef, actionedUponNodeRef);
	    }
	}
	
	/**
     * Get the action executor instance.
     * 
     * @param action	the action
     * @return			the action executor
     */
    private RuleActionExecuter getActionExecutor(RuleAction action)
    {
        String executorString = ((RuleActionDefinitionImpl)action.getRuleActionDefinition()).getRuleActionExecutor();
        return (RuleActionExecuter)applicationContext.getBean(executorString);
    }

	/**
	 * Get the condition evaluator.
	 * 
	 * @param cond	the rule condition
	 * @return		the rule condition evaluator
	 */
    private RuleConditionEvaluator getConditionEvaluator(RuleCondition cond)
    {
        String evaluatorString = ((RuleConditionDefinitionImpl)cond.getRuleConditionDefinition()).getConditionEvaluator();
        return (RuleConditionEvaluator)this.applicationContext.getBean(evaluatorString);
    }
	
	/**
	 * Register the rule type
	 * 
	 * @param ruleTypeAdapter  the rule type adapter
	 */
	public void registerRuleType(RuleTypeAdapter ruleTypeAdapter) 
	{
		RuleType ruleType = ruleTypeAdapter.getRuleType();
		this.ruleTypes.put(ruleType.getName(), ruleType);
	}
	
	/**
	 * Register the condition definition
	 * 
	 * @param ruleConditionEvaluator  the rule condition evaluator
	 */
	public void registerRuleConditionEvaluator(RuleConditionEvaluator ruleConditionEvaluator) 
	{
		RuleConditionDefinition cond = ruleConditionEvaluator.getRuleConditionDefintion();
		this.conditionDefinitions.put(cond.getName(), cond);
	}

	/**
	 * Register the action executor
	 * 
	 * @param ruleActionExecuter	the rule action executor
	 */
	public void registerRuleActionExecuter(RuleActionExecuter ruleActionExecuter) 
	{
		RuleActionDefinition action = ruleActionExecuter.getRuleActionDefinition();
		this.actionDefinitions.put(action.getName(), action);
	}
    
	/**
	 * On copy complete behaviour implementation
	 */
	public void onCopyComplete(
			QName classRef,
			NodeRef sourceNodeRef,
			NodeRef destinationRef,
			Map<NodeRef, NodeRef> copyMap)
	{
		Set<Rule> modifiedRules = new HashSet<Rule>();
		List<? extends Rule> rules = this.ruleStore.get(destinationRef, false);
		for (Rule rule : rules)
		{
			// Check all the rule actions 
			for (RuleAction ruleAction : rule.getRuleActions())
			{
				for (ParameterDefinition paramDef : ruleAction.getRuleActionDefinition().getParameterDefinitions())
				{
					if (ParameterType.NODE_REF.equals(paramDef.getType()))
					{
						NodeRef value = (NodeRef)ruleAction.getParameterValue(paramDef.getName());
						if (value != null && copyMap.containsKey(value) == true)
						{
							// Change the parameter value to be relative
							ruleAction.setParameterValue(paramDef.getName(), copyMap.get(value));
							modifiedRules.add(rule);
						}
					}
				}
			}
			
			// Check all the rule conditions
			for (RuleCondition ruleCondition : rule.getRuleConditions())
			{
				for (ParameterDefinition paramDef : ruleCondition.getRuleConditionDefinition().getParameterDefinitions())
				{
					if (ParameterType.NODE_REF.equals(paramDef.getType()))
					{
						NodeRef value = (NodeRef)ruleCondition.getParameterValue(paramDef.getName());
						if (value != null && copyMap.containsKey(value) == true)
						{
							// Change the parameter value to be relative
							ruleCondition.setParameterValue(paramDef.getName(), copyMap.get(value));
							modifiedRules.add(rule);
						}
					}
				}
			}
		}
		
		// Update any of the rules that have been modified
		for (Rule modifiedRule : modifiedRules)
		{
			this.ruleStore.put(destinationRef, (RuleImpl)modifiedRule);
		}
	}
    
    /**
     * Helper class to contain the information about a rule that is executed
     * 
     * @author Roy Wetherall
     */
    private class ExecutedRuleData
    {

        protected NodeRef actionableNodeRef;
        protected Rule rule;
        
        public ExecutedRuleData(NodeRef actionableNodeRef, Rule rule) 
        {
            this.actionableNodeRef = actionableNodeRef;
            this.rule = rule;
        }

        public NodeRef getActionableNodeRef()
        {
        	return actionableNodeRef;
        }

        public Rule getRule()
        {
        	return rule;
        }
        
        @Override
        public int hashCode() 
        {
            int i = actionableNodeRef.hashCode();
            i = (i*37) + rule.hashCode();
            return i;
        }
        
        @Override
        public boolean equals(Object obj) 
        {
            if (this == obj)
            {
                return true;
            }
            if (obj instanceof ExecutedRuleData)
            {
                ExecutedRuleData that = (ExecutedRuleData) obj;
                return (this.actionableNodeRef.equals(that.actionableNodeRef) &&
                        this.rule.equals(that.rule));
            }
            else
            {
                return false;
            }
        }
    }

	/**
	 * Helper class to contain the information about a rule that is pending execution
	 * 
	 * @author Roy Wetherall
	 */
	private class PendingRuleData extends ExecutedRuleData
	{
		private NodeRef actionedUponNodeRef;
        
		public PendingRuleData(NodeRef actionableNodeRef, NodeRef actionedUponNodeRef, Rule rule) 
		{
			super(actionableNodeRef, rule);
			this.actionedUponNodeRef = actionedUponNodeRef;
		}
		
		public NodeRef getActionedUponNodeRef() 
		{
			return actionedUponNodeRef;
		}
		
		@Override
		public int hashCode() 
		{
			int i = super.hashCode();
			i = (i*37) + actionedUponNodeRef.hashCode();
			return i;
		}
		
		@Override
		public boolean equals(Object obj) 
		{
			if (this == obj)
	        {
	            return true;
	        }
	        if (obj instanceof PendingRuleData)
	        {
				PendingRuleData that = (PendingRuleData) obj;
	            return (this.actionableNodeRef.equals(that.actionableNodeRef) &&
	                    this.actionedUponNodeRef.equals(that.actionedUponNodeRef) &&
	                    this.rule.equals(that.rule));
	        }
	        else
	        {
	            return false;
	        }
		}
	}	
}
