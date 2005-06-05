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
import org.alfresco.repo.content.ContentService;
import org.alfresco.repo.node.NodeService;
import org.alfresco.repo.ref.NodeRef;
import org.alfresco.repo.rule.Rule;
import org.alfresco.repo.rule.RuleActionDefinition;
import org.alfresco.repo.rule.RuleConditionDefinition;
import org.alfresco.repo.rule.RuleService;
import org.alfresco.repo.rule.RuleType;
import org.alfresco.util.GUID;

/**
 * @author Roy Wetherall
 */
public class RuleServiceImpl implements RuleService
{
    /**
     * The config service
     */
    private ConfigService configService;
    
    private NodeService nodeService;
    
    private ContentService contentService;
    
    private RuleConfig ruleConfiguration;
    
    private RuleStore ruleStore;
    
    
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
     * @see org.alfresco.repo.rule.RuleService#getRuleTypes()
     */
    public List<RuleType> getRuleTypes()
    {
        Collection<RuleTypeImpl> ruleTypes = this.ruleConfiguration.getRuleTypes();
        ArrayList<RuleType> result = new ArrayList<RuleType>(ruleTypes.size());
        result.addAll(ruleTypes);
        return result;
    }

    /**
     * @see org.alfresco.repo.rule.RuleService#getConditionDefinitions()
     */
    public List<RuleConditionDefinition> getConditionDefinitions()
    {
        Collection<RuleConditionDefinitionImpl> items = this.ruleConfiguration.getConditionDefinitions();
        ArrayList<RuleConditionDefinition> result = new ArrayList<RuleConditionDefinition>(items.size());
        result.addAll(items);
        return result;
    }

    /**
     * @see org.alfresco.repo.rule.RuleService#getActionDefinitions()
     */
    public List<RuleActionDefinition> getActionDefinitions()
    {
        Collection<RuleActionDefinitionImpl> items = this.ruleConfiguration.getActionDefinitions();
        ArrayList<RuleActionDefinition> result = new ArrayList<RuleActionDefinition>(items.size());
        result.addAll(items);
        return result;
    }

    /**
     * @see org.alfresco.repo.rule.RuleService#makeActionable(org.alfresco.repo.ref.NodeRef, org.alfresco.repo.ref.NodeRef)
     */
    public void makeActionable(
            NodeRef nodeRef, 
            NodeRef ruleDefinitionFolderParent)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.alfresco.repo.rule.RuleService#isActionable(org.alfresco.repo.ref.NodeRef)
     */
    public boolean isActionable(NodeRef nodeRef)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.alfresco.repo.rule.RuleService#getRules(org.alfresco.repo.ref.NodeRef)
     */
    public List<RuleImpl> getRules(NodeRef nodeRef)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.alfresco.repo.rule.RuleService#getRules(org.alfresco.repo.ref.NodeRef, boolean)
     */
    public List<RuleImpl> getRules(NodeRef nodeRef, boolean includeInhertied)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.alfresco.repo.rule.RuleService#previewExecutingRules(org.alfresco.repo.ref.NodeRef, org.alfresco.repo.rule.RuleType, java.util.Map)
     */
    public List<RuleImpl> previewExecutingRules(NodeRef nodeRef, RuleType ruleType, Map<String, Serializable> executionContext)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.alfresco.repo.rule.RuleService#createRule(org.alfresco.repo.rule.RuleType)
     */
    public Rule createRule(RuleType ruleType)
    {
        String id = GUID.generate();
        return new RuleImpl(id, ruleType);
    }

    /**
     * @see org.alfresco.repo.rule.RuleService#addRule(org.alfresco.repo.ref.NodeRef, org.alfresco.repo.rule.Rule)
     */
    public void addRule(NodeRef nodeRef, Rule rule)
    {
        throw new UnsupportedOperationException();
    }
    
    /**
     * @see org.alfresco.repo.rule.RuleService#removeRule(org.alfresco.repo.ref.NodeRef, org.alfresco.repo.rule.impl.RuleImpl)
     */
    public void removeRule(NodeRef nodeRef, Rule rule)
    {
        throw new UnsupportedOperationException();
    } 
}
