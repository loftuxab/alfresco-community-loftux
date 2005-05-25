/**
 * 
 */
package org.alfresco.repo.rule.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.alfresco.repo.ref.NodeRef;
import org.alfresco.repo.rule.Rule;
import org.alfresco.repo.rule.RuleAction;
import org.alfresco.repo.rule.RuleCondition;
import org.alfresco.repo.rule.RuleService;
import org.alfresco.repo.rule.RuleType;

/**
 * @author Roy Wetherall
 */
public class RuleServiceImpl implements RuleService{

    /**
     * 
     */
    public RuleServiceImpl()
    {
    }

    /**
     * @see org.alfresco.repo.rule.RuleService#getRuleTypes()
     */
    public List<RuleType> getRuleTypes()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.alfresco.repo.rule.RuleService#getConditionDefintions()
     */
    public List<RuleCondition> getConditionDefintions()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.alfresco.repo.rule.RuleService#getActionDefinitions()
     */
    public List<RuleAction> getActionDefinitions()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.alfresco.repo.rule.RuleService#makeActionable(org.alfresco.repo.ref.NodeRef, org.alfresco.repo.ref.NodeRef)
     */
    public void makeActionable(NodeRef nodeRef, NodeRef ruleDefinitionFolderParent)
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
    public List<Rule> getRules(NodeRef nodeRef)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.alfresco.repo.rule.RuleService#getRules(org.alfresco.repo.ref.NodeRef, boolean)
     */
    public List<Rule> getRules(NodeRef nodeRef, boolean includeInhertied)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.alfresco.repo.rule.RuleService#previewExecutingRules(org.alfresco.repo.ref.NodeRef, org.alfresco.repo.rule.RuleType, java.util.Map)
     */
    public List<Rule> previewExecutingRules(NodeRef nodeRef, RuleType ruleType, Map<String, Serializable> executionContext)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.alfresco.repo.rule.RuleService#updateRule(org.alfresco.repo.ref.NodeRef, org.alfresco.repo.rule.Rule)
     */
    public void updateRule(NodeRef nodeRef, Rule rule)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.alfresco.repo.rule.RuleService#removeRule(org.alfresco.repo.ref.NodeRef, org.alfresco.repo.rule.Rule)
     */
    public void removeRule(NodeRef nodeRef, Rule rule)
    {
        throw new UnsupportedOperationException();
    }
}
