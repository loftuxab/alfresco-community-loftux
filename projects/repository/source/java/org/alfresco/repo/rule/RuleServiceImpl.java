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
package org.alfresco.repo.rule;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.action.ActionableAspect;
import org.alfresco.repo.action.RuntimeActionService;
import org.alfresco.repo.transaction.AlfrescoTransactionSupport;
import org.alfresco.repo.transaction.TransactionListener;
import org.alfresco.service.cmr.action.ActionService;
import org.alfresco.service.cmr.action.ActionServiceException;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.rule.Rule;
import org.alfresco.service.cmr.rule.RuleService;
import org.alfresco.service.cmr.rule.RuleServiceException;
import org.alfresco.service.cmr.rule.RuleType;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.DynamicNamespacePrefixResolver;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.GUID;

/**
 * Rule service implementation.
 * <p>
 * This service automatically binds to the transaction flush hooks.  It will
 * therefore participate in any flushes that occur during the transaction as
 * well.
 * 
 * @author Roy Wetherall   
 */
public class RuleServiceImpl implements RuleService, RuntimeRuleService
{
    /** key against which to store rules pending on the current transaction */
    private static final String KEY_RULES_PENDING = "RuleServiceImpl.PendingRules";
    
    /** key against which to store executed rules on the current transaction */
    private static final String KEY_RULES_EXECUTED = "RuleServiceImpl.ExecutedRules";
    
    /**
     * The node service
     */
    private NodeService nodeService;
    
    /**
     * The action service
     */
    private ActionService actionService;
    
    /**
     * The search service
     */
    private SearchService searchService;
    
    /**
     * The rule cahce (set by default to an inactive rule cache)
     */
    private RuleCache ruleCache = new InactiveRuleCache();
       
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
	 * The rule transaction listener
	 */
	private TransactionListener ruleTransactionListener = new RuleTransactionListener(this);      
    
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
     * Set the action service
     * 
     * @param actionRegistration  the action service
     */
    public void setActionService(ActionService actionService)
	{
		this.actionService = actionService;
	}
    
    /**
     * Set the search service
     * 
     * @param searchService   the search service
     */
    public void setSearchService(SearchService searchService)
	{
		this.searchService = searchService;
	}
    
    /**
     * Set the rule cache
     * 
     * @param ruleCache  the rule cache
     */
    public void setRuleCache(RuleCache ruleCache)
	{
		this.ruleCache = ruleCache;
	}
	
	/**
	 * Gets the saved rule folder reference
	 * 
	 * @param nodeRef	the node reference
	 * @return			the node reference
	 */
	private NodeRef getSavedRuleFolderRef(NodeRef nodeRef)
	{
		List<ChildAssociationRef> assocs = this.nodeService.getChildAssocs(nodeRef, ActionableAspect.ASSOC_NAME_SAVEDRULESFOLDER);
		if (assocs.size() != 1)
		{
			throw new ActionServiceException("Unable to retrieve the saved rule folder reference.");
		}
		
		return assocs.get(0).getChildRef();
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
     * @see org.alfresco.service.cmr.rule.RuleService#hasRules(org.alfresco.repo.ref.NodeRef)
     */
    public boolean hasRules(NodeRef nodeRef)
    {
    	return (getRules(nodeRef).size() != 0);
    } 

    /**
     * @see org.alfresco.repo.rule.RuleService#getRules(org.alfresco.repo.ref.NodeRef)
     */
    public List<Rule> getRules(NodeRef nodeRef)
    {
    	return getRules(nodeRef, true, null);
    }

    /**  
     * @see org.alfresco.repo.rule.RuleService#getRules(org.alfresco.repo.ref.NodeRef, boolean)
     */
    public List<Rule> getRules(NodeRef nodeRef, boolean includeInherited)
    {
    	return getRules(nodeRef, includeInherited, null);
    }
    
    /**
     * @see org.alfresco.repo.rule.RuleService#getRulesByRuleType(org.alfresco.repo.ref.NodeRef, org.alfresco.repo.rule.RuleType)
     */
    public List<Rule> getRules(NodeRef nodeRef, boolean includeInherited, String ruleTypeName)
    {
    	List<Rule> rules = new ArrayList<Rule>();
    	
    	if (this.nodeService.exists(nodeRef) == true)
    	{
    		if (includeInherited == true)
    		{
    			// Get any inhertied rules
    			for (Rule rule : getInheritedRules(nodeRef, ruleTypeName, null))
				{
    				// Ensure rules are not duplicated in the list
    				if (rules.contains(rule) == false)
    				{
    					rules.add(rule);
    				}
				}
    		}
    		
    		// If the node is actionable then get any rules that it might have set against it
    		if (this.nodeService.hasAspect(nodeRef, ContentModel.ASPECT_ACTIONABLE) == true)
    		{
    			List<Rule> allRules = this.ruleCache.getRules(nodeRef);
    			if (allRules == null)
    			{
    				allRules = new ArrayList<Rule>();
    				
		    		// Get the rules for this node
		    		List<ChildAssociationRef> ruleChildAssocRefs = 
		    			this.nodeService.getChildAssocs(getSavedRuleFolderRef(nodeRef), ActionableAspect.ASSOC_NAME_SAVEDRULESFOLDER);
		    		for (ChildAssociationRef ruleChildAssocRef : ruleChildAssocRefs)
					{
		    			// Create the rule and add to the list
						NodeRef ruleNodeRef = ruleChildAssocRef.getChildRef();
						Rule rule = createRule(ruleNodeRef);
						allRules.add(rule);
					}
		    		
		    		// Add the list to the cache
		    		this.ruleCache.setRules(nodeRef, allRules);
    			}
    			
    			// Build the list of rules that is returned to the client
    			for (Rule rule : allRules)
				{					
					if ((rules.contains(rule) == false) &&
					    (ruleTypeName == null || ruleTypeName.equals(rule.getRuleTypeName()) == true))
					{
						rules.add(rule);						
					}
				}
    		}
    	}
    	
    	return rules;
    }
	
    /**
     * Gets the inherited rules for a given node reference
     * 
     * @param nodeRef			the nodeRef
     * @param ruleTypeName		the rule type (null if all applicable)
     * @return					a list of inherited rules (empty if none)
     */
	private List<Rule> getInheritedRules(NodeRef nodeRef, String ruleTypeName, Set<NodeRef> visitedNodeRefs)
	{
		List<Rule> inheritedRules = new ArrayList<Rule>();
		
		// Create the vistied nodes set if it has not already been created
		if (visitedNodeRefs == null)
		{
			visitedNodeRefs = new HashSet<NodeRef>();
		}
		
		// This check prevents stack over flow when we have a cyclic node graph
		if (visitedNodeRefs.contains(nodeRef) == false)
		{
			visitedNodeRefs.add(nodeRef);
			
			List<Rule> allInheritedRules = this.ruleCache.getInheritedRules(nodeRef);
			if (allInheritedRules == null)
			{
				allInheritedRules = new ArrayList<Rule>();
				List<ChildAssociationRef> parents = this.nodeService.getParentAssocs(nodeRef);
				for (ChildAssociationRef parent : parents)
				{
					List<Rule> rules = getRules(parent.getParentRef(), false);
					for (Rule rule : rules)
					{
						// Add is we hanvn't already added and it should be applied to the children
						if (rule.isAppliedToChildren() == true && allInheritedRules.contains(rule) == false)
						{
							allInheritedRules.add(rule);
						}
					}
					
					for (Rule rule : getInheritedRules(parent.getParentRef(), ruleTypeName, visitedNodeRefs))
					{
						// Ensure that we don't get any rule duplication (don't use a set cos we want to preserve order)
						if (allInheritedRules.contains(rule) == false)
						{
							allInheritedRules.add(rule);
						}
					}
				}
				
				// Add the list of inherited rules to the cache
				this.ruleCache.setInheritedRules(nodeRef, allInheritedRules);
			}
			
			if (ruleTypeName == null)
			{
				inheritedRules = allInheritedRules;
			}
			else
			{
				// Filter the rule list by rule type
				for (Rule rule : allInheritedRules)
				{
					if (rule.getRuleTypeName().equals(ruleTypeName) == true)
					{
						inheritedRules.add(rule);
					}
				}
			}
		}
		
		return inheritedRules;
	}

	/**
	 * @see org.alfresco.repo.rule.RuleService#getRule(String) 
	 */
	public Rule getRule(NodeRef nodeRef, String ruleId) 
	{
		Rule rule = null;
		
		if (this.nodeService.exists(nodeRef) == true)
		{
			NodeRef ruleNodeRef = getRuleNodeRefFromId(nodeRef, ruleId);
			if (ruleNodeRef != null)
			{
				rule = createRule(ruleNodeRef);
			}
		}
		
		return rule;
	}    
	
	/**
	 * Gets the rule node ref from the action id
	 * 
	 * @param nodeRef	the node reference
	 * @param actionId	the rule id
	 * @return			the rule node reference
	 */
	private NodeRef getRuleNodeRefFromId(NodeRef nodeRef, String ruleId)
	{
		NodeRef result = null;
		if (this.nodeService.hasAspect(nodeRef, ContentModel.ASPECT_ACTIONABLE) == true)
		{
			DynamicNamespacePrefixResolver namespacePrefixResolver = new DynamicNamespacePrefixResolver();
			namespacePrefixResolver.registerNamespace(NamespaceService.SYSTEM_MODEL_PREFIX, NamespaceService.SYSTEM_MODEL_1_0_URI);
			
			List<NodeRef> nodeRefs = searchService.selectNodes(
					getSavedRuleFolderRef(nodeRef), 
					"*[@sys:" + ContentModel.PROP_NODE_UUID.getLocalName() + "='" + ruleId + "']",
					null,
					namespacePrefixResolver,
					false);
			if (nodeRefs.size() != 0)
			{
				result = nodeRefs.get(0);
			}
		}
		
		return result;
	}

	/**
	 * Create the rule object from the rule node reference
	 * 
	 * @param ruleNodeRef	the rule node reference
	 * @return				the rule
	 */
    private Rule createRule(NodeRef ruleNodeRef)
	{
    	// Get the rule properties
		Map<QName, Serializable> props = this.nodeService.getProperties(ruleNodeRef);
		
    	// Create the rule
    	String ruleTypeName = (String)props.get(ContentModel.PROP_RULE_TYPE);    	
		Rule rule = new RuleImpl(ruleNodeRef.getId(), ruleTypeName);
		
		// Set the other rule properties
		boolean isAppliedToChildren = ((Boolean)props.get(ContentModel.PROP_APPLY_TO_CHILDREN)).booleanValue();
		rule.applyToChildren(isAppliedToChildren);
		
		// Populate the composite action details
		((RuntimeActionService)this.actionService).populateCompositeAction(ruleNodeRef, rule);
		
		return rule;
	}

	/**
     * @see org.alfresco.repo.rule.RuleService#createRule(org.alfresco.repo.rule.RuleType)
     */
    public Rule createRule(String ruleTypeName)
    {
        // Create the new rule, giving it a unique rule id
        String id = GUID.generate();
        return new RuleImpl(id, ruleTypeName);
    }

    /**
     * @see org.alfresco.repo.rule.RuleService#saveRule(org.alfresco.repo.ref.NodeRef, org.alfresco.repo.rule.Rule)
     */
    public void saveRule(NodeRef nodeRef, Rule rule)
    {
    	if (this.nodeService.exists(nodeRef) == false)
    	{
    		throw new RuleServiceException("The node does not exist.");
    	}
    	
    	NodeRef ruleNodeRef = getRuleNodeRefFromId(nodeRef, rule.getId());
    	if (ruleNodeRef == null)
    	{
    		if (this.nodeService.hasAspect(nodeRef, ContentModel.ASPECT_ACTIONABLE) == false)
    		{
    			// Add the actionable aspect
    			this.nodeService.addAspect(nodeRef, ContentModel.ASPECT_ACTIONABLE, null);
    		}
    		
    		Map<QName, Serializable> props = new HashMap<QName, Serializable>(3);
    		props.put(ContentModel.PROP_RULE_TYPE, rule.getRuleTypeName());
			props.put(ContentModel.PROP_DEFINITION_NAME, rule.getActionDefinitionName());
			props.put(ContentModel.PROP_NODE_UUID, rule.getId());
			props.put(ContentModel.PROP_APPLY_TO_CHILDREN, rule.isAppliedToChildren());
			
			// Create the action node
			ruleNodeRef = this.nodeService.createNode(
					getSavedRuleFolderRef(nodeRef),
					ContentModel.ASSOC_SAVED_ACTIONS,
					ActionableAspect.ASSOC_NAME_SAVEDRULESFOLDER,
					ContentModel.TYPE_RULE,
					props).getChildRef();
			
			// Update the created details
			((RuleImpl)rule).setCreator((String)this.nodeService.getProperty(ruleNodeRef, ContentModel.PROP_CREATOR));
			((RuleImpl)rule).setCreatedDate((Date)this.nodeService.getProperty(ruleNodeRef, ContentModel.PROP_CREATED));
    	}
    	
    	// Save the remainder of the rule as a composite action
    	((RuntimeActionService)this.actionService).saveActionImpl(ruleNodeRef, rule);
    }
    
    /**
     * @see org.alfresco.repo.rule.RuleService#removeRule(org.alfresco.repo.ref.NodeRef, org.alfresco.repo.rule.RuleImpl)
     */
    public void removeRule(NodeRef nodeRef, Rule rule)
    {
    	if (this.nodeService.exists(nodeRef) == true &&
    		this.nodeService.hasAspect(nodeRef, ContentModel.ASPECT_ACTIONABLE) == true)
    	{
    		NodeRef ruleNodeRef = getRuleNodeRefFromId(nodeRef, rule.getId());
    		if (ruleNodeRef != null)
    		{
    			this.nodeService.removeChild(getSavedRuleFolderRef(nodeRef), ruleNodeRef);
    		}
    	}
    }	
    
    /**
     * @see org.alfresco.repo.rule.RuleService#removeAllRules(NodeRef)
     */
    public void removeAllRules(NodeRef nodeRef)
    {
    	if (this.nodeService.exists(nodeRef) == true && 
        	this.nodeService.hasAspect(nodeRef, ContentModel.ASPECT_ACTIONABLE) == true)
    	{
    		List<ChildAssociationRef> ruleChildAssocs = this.nodeService.getChildAssocs(getSavedRuleFolderRef(nodeRef), ActionableAspect.ASSOC_NAME_SAVEDRULESFOLDER);
    		for (ChildAssociationRef ruleChildAssoc : ruleChildAssocs)
			{
				this.nodeService.removeChild(getSavedRuleFolderRef(nodeRef), ruleChildAssoc.getChildRef());
			}
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
                    // bind the rule transaction listener
                    AlfrescoTransactionSupport.bindListener(this.ruleTransactionListener);
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
	      
		// Evaluate the condition
	    if (this.actionService.evaluateAction(rule, actionedUponNodeRef) == true)
	    {
            // Add the rule to the executed rule list
            // (do this before this is executed to prevent rules being added to the pending list) 
            Set<ExecutedRuleData> executedRules =
                    (Set<ExecutedRuleData>) AlfrescoTransactionSupport.getResource(KEY_RULES_EXECUTED);
            executedRules.add(new ExecutedRuleData(actionableNodeRef, rule));
            
			// Execute the rule
            this.actionService.executeAction(rule, actionedUponNodeRef);
	    }
	}
	
	/**
	 * Register the rule type
	 * 
	 * @param ruleTypeAdapter  the rule type adapter
	 */
	public void registerRuleType(RuleType ruleType) 
	{
		this.ruleTypes.put(ruleType.getName(), ruleType);
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
	
	/**
	 * Inactive rule cache
	 * 
	 * @author Roy Wetherall
	 */
	private class InactiveRuleCache implements RuleCache
	{
		/**
		 * @see org.alfresco.repo.rule.RuleCache#getRules(org.alfresco.service.cmr.repository.NodeRef)
		 */
		public List<Rule> getRules(NodeRef nodeRef)
		{
			// do nothing
			return null;
		}

		/**
		 *  @see org.alfresco.repo.rule.RuleCache#setRules(org.alfresco.service.cmr.repository.NodeRef, List<Rule>)
		 */
		public void setRules(NodeRef nodeRef, List<Rule> rules)
		{
			// do nothing
		}

		/**
		 * @see org.alfresco.repo.rule.RuleCache#dirtyRules(org.alfresco.service.cmr.repository.NodeRef)
		 */
		public void dirtyRules(NodeRef nodeRef)
		{
			// do nothing
		}

		/**
		 * @see org.alfresco.repo.rule.RuleCache#getInheritedRules(org.alfresco.service.cmr.repository.NodeRef)
		 */
		public List<Rule> getInheritedRules(NodeRef nodeRef)
		{
			// do nothing
			return null;
		}

		/**
		 * @see org.alfresco.repo.rule.RuleCache#setInheritedRules(org.alfresco.service.cmr.repository.NodeRef, List<Rule>)
		 */
		public void setInheritedRules(NodeRef nodeRef, List<Rule> rules)
		{
			// do nothing
		}
	}
}
