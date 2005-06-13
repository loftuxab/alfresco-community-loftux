/**
 * 
 */
package org.alfresco.repo.rule.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.alfresco.config.ConfigService;
import org.alfresco.repo.dictionary.impl.DictionaryBootstrap;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.rule.RuleTypeAdapter;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.rule.Rule;
import org.alfresco.service.cmr.rule.RuleActionDefinition;
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
 * 
 * @author Roy Wetherall   
 */
public class RuleServiceImpl implements RuleService, ApplicationContextAware
{
    /**
     * The config service
     */
    private ConfigService configService;
    
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
     * The policy component
     */
    private PolicyComponent policyComponent;
    
    /**
     * The rule config
     */
    private RuleConfig ruleConfiguration;
    
    /**
     * The rule store
     */
    private RuleStore ruleStore;
    
    /**
     * List of rule type adapters
     */
    private List<RuleTypeAdapter> adapters;

    /**
     * The application context
     */
    private ApplicationContext applicationContext;
    
    /**
     * Service intialization method
     */
    public void init()
    {
        // Create the rule configuration and store
        this.ruleConfiguration = new RuleConfig(this.configService);
        this.ruleStore = new RuleStore(
                this.nodeService, 
                this.contentService,
                this.ruleConfiguration);
        
        // Initialise the rule types
        initRuleTypes();
    }

    /**
     * Initialise the rule types
     */
    private void initRuleTypes()
    {
        // Register the rule type policy bahviours
        List<RuleType> ruleTypes = getRuleTypes();
        List<RuleTypeAdapter> adapters = new ArrayList<RuleTypeAdapter>(ruleTypes.size());
        for (RuleType ruleType : ruleTypes)
        {
            // Create the rule type adapter and register policy bahaviours
            String ruleTypeAdapter = ((RuleTypeImpl)ruleType).getRuleTypeAdapter();
            if (ruleTypeAdapter != null && ruleTypeAdapter.length() != 0)
            {
                try 
                {
                    // Create the rule type adapter
                    RuleTypeAdapter adapter = (RuleTypeAdapter)Class.forName(ruleTypeAdapter).
                            getConstructor(new Class[]{RuleType.class, PolicyComponent.class, RuleService.class, NodeService.class}).
                            newInstance(new Object[]{ruleType, this.policyComponent, this, this.nodeService});
                    
                    // Register the adapters policy behaviour
                    adapter.registerPolicyBehaviour();
                }
                catch(Exception exception)
                {
                    // Error creating and initialising the adapter
                    throw new RuleServiceException("Unable to initialise the rule type adapter.", exception);
                }
            }
        }
    }       

    /**
     * Set the config service
     * 
     * @param configService     the config service
     */
    public void setConfigService(ConfigService configService)
    {
        this.configService = configService;
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
     * Sets the policy component
     * 
     * @param policyComponent  the policy component
     */
    public void setPolicyComponent(PolicyComponent policyComponent)
    {
        this.policyComponent = policyComponent;
    }
    
    /**
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    public void setApplicationContext(ApplicationContext applicationContext) 
        throws BeansException
    {
        this.applicationContext = applicationContext;
    }
    
    /**
     * @see org.alfresco.repo.rule.RuleService#getRuleTypes()
     */
    public List<RuleType> getRuleTypes()
    {
        // Get the rule types from the rule config
        Collection<RuleTypeImpl> ruleTypes = this.ruleConfiguration.getRuleTypes();
        ArrayList<RuleType> result = new ArrayList<RuleType>(ruleTypes.size());
        result.addAll(ruleTypes);
        return result;
    }
    
    /**
     * @see org.alfresco.repo.rule.RuleService#getRuleType(java.lang.String)
     */
    public RuleType getRuleType(String name)
    {
        return this.ruleConfiguration.getRuleType(name);
    }

    /**
     * @see org.alfresco.repo.rule.RuleService#getConditionDefinitions()
     */
    public List<RuleConditionDefinition> getConditionDefinitions()
    {
        // Get the condition defintion from the rule config
        Collection<RuleConditionDefinitionImpl> items = this.ruleConfiguration.getConditionDefinitions();
        ArrayList<RuleConditionDefinition> result = new ArrayList<RuleConditionDefinition>(items.size());
        result.addAll(items);
        return result;
    }
    
    /**
     * @see org.alfresco.repo.rule.RuleService#getConditionDefintion(java.lang.String)
     */
    public RuleConditionDefinition getConditionDefintion(String name)
    {
        return this.ruleConfiguration.getConditionDefinition(name);
    }

    /**
     * @see org.alfresco.repo.rule.RuleService#getActionDefinitions()
     */
    public List<RuleActionDefinition> getActionDefinitions()
    {
        // Get the rule action defintions from the config
        Collection<RuleActionDefinitionImpl> items = this.ruleConfiguration.getActionDefinitions();
        ArrayList<RuleActionDefinition> result = new ArrayList<RuleActionDefinition>(items.size());
        result.addAll(items);
        return result;
    }

    /**
     * @see org.alfresco.repo.rule.RuleService#getActionDefinition(java.lang.String)
     */
    public RuleActionDefinition getActionDefinition(String name)
    {
        return this.ruleConfiguration.getActionDefinition(name);
    }

    /**
     * @see org.alfresco.repo.rule.RuleService#makeActionable(org.alfresco.repo.ref.NodeRef, org.alfresco.repo.ref.NodeRef)
     */
    public void makeActionable(
            NodeRef nodeRef)
    {
        // Get the root config node
		NodeRef rootConfigFolder = getRootConfigNodeRef(nodeRef);
		
		// Create the configuraion folder
		NodeRef configurationsNodeRef = this.nodeService.createNode(
											rootConfigFolder,
											DictionaryBootstrap.CHILD_ASSOC_QNAME_CONTAINS,
											QName.createQName(NamespaceService.ALFRESCO_URI, "configurations"),
											DictionaryBootstrap.TYPE_QNAME_CONFIGURATIONS).getChildRef();
		
        // Apply the aspect and add the configurations folder
        this.nodeService.addAspect(
                nodeRef, 
                DictionaryBootstrap.ASPECT_QNAME_ACTIONABLE, 
                null);
        this.nodeService.createAssociation(
                nodeRef, 
                configurationsNodeRef, 
                DictionaryBootstrap.ASSOC_QNAME_CONFIGURATIONS);	
    }

	/**
	 * Get the root config node reference
	 * 
	 * @param nodeRef	the node reference
	 * @return			the root config node reference
	 */
	private NodeRef getRootConfigNodeRef(NodeRef nodeRef) 
	{
		// TODO maybe this should be cached ...
		// TODO the QNames should be put in the DicitionaryBootstrap
		
		NodeRef rootConfigFolder = null;
		NodeRef rootNode = this.nodeService.getRootNode(nodeRef.getStoreRef());
		List<ChildAssociationRef> childAssocRefs = this.nodeService.getChildAssocs(
							  					rootNode, 
												QName.createQName(NamespaceService.ALFRESCO_URI, "systemconfiguration"));
		if (childAssocRefs.size() == 0)
		{
			rootConfigFolder = this.nodeService.createNode(
												rootNode,
												DictionaryBootstrap.CHILD_ASSOC_QNAME_CHILDREN,
												QName.createQName(NamespaceService.ALFRESCO_URI, "systemconfiguration"),
												DictionaryBootstrap.TYPE_QNAME_SYTEM_FOLDER).getChildRef();
		}
		else
		{
			rootConfigFolder = childAssocRefs.get(0).getChildRef();
		}
		return rootConfigFolder;
	}

    /**
     * @see org.alfresco.repo.rule.RuleService#isActionable(org.alfresco.repo.ref.NodeRef)
     */
    public boolean isActionable(NodeRef nodeRef)
    {
        // Determine whether a node is actionable or not
        return (this.nodeService.hasAspect(nodeRef, DictionaryBootstrap.ASPECT_QNAME_ACTIONABLE) == true);          
    }
    
    /**
     * @see org.alfresco.repo.rule.RuleService#hasRules(org.alfresco.repo.ref.NodeRef)
     */
    public boolean hasRules(NodeRef nodeRef)
    {
        return this.ruleStore.hasRules(nodeRef);
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
    public List<Rule> getRules(NodeRef nodeRef, boolean includeInhertied)
    {
        return (List<Rule>)this.ruleStore.get(nodeRef, includeInhertied);
    }

    /**
     * @see org.alfresco.repo.rule.RuleService#getRulesByRuleType(org.alfresco.repo.ref.NodeRef, org.alfresco.repo.rule.RuleType)
     */
    public List<Rule> getRulesByRuleType(NodeRef nodeRef, RuleType ruleType)
    {
        return (List<Rule>)this.ruleStore.getByRuleType(nodeRef, ruleType);
    }

    /**
     * @see org.alfresco.repo.rule.RuleService#previewExecutingRules(org.alfresco.repo.ref.NodeRef, org.alfresco.repo.rule.RuleType, java.util.Map)
     */
    public List<Rule> previewExecutingRules(NodeRef nodeRef, RuleType ruleType, Map<String, Serializable> executionContext)
    {
        throw new UnsupportedOperationException();
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
     * @see org.alfresco.repo.rule.RuleService#removeRule(org.alfresco.repo.ref.NodeRef, org.alfresco.repo.rule.impl.RuleImpl)
     */
    public void removeRule(NodeRef nodeRef, Rule rule)
    {
        // Remove the rule from the rule store
        this.ruleStore.remove(nodeRef, (RuleImpl)rule);
    }     
}
