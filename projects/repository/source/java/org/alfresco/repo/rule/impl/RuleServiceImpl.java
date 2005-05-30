/**
 * 
 */
package org.alfresco.repo.rule.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.alfresco.repo.ref.NodeRef;
import org.alfresco.repo.rule.Rule;
import org.alfresco.repo.rule.RuleActionDefinition;
import org.alfresco.repo.rule.RuleConditionDefinition;
import org.alfresco.repo.rule.RuleService;
import org.alfresco.repo.rule.RuleType;

/**
 * @author Roy Wetherall
 */
public class RuleServiceImpl implements RuleService{

    /**
     * @see org.alfresco.repo.rule.RuleService#getRuleTypes()
     */
    public List<RuleType> getRuleTypes()
    {
        // TODO tempory result to aid UI development
        ArrayList<RuleType> ruleTypes = new ArrayList<RuleType>(2);
        ruleTypes.add(new RuleTypeImpl("inbound", "Inbound"));
        ruleTypes.add(new RuleTypeImpl("outbound", "Outbound"));
        return ruleTypes;
    }

    /**
     * @see org.alfresco.repo.rule.RuleService#getConditionDefinitions()
     */
    public List<RuleConditionDefinition> getConditionDefinitions()
    {
        // TODO tempory result to aid UI development
        ArrayList<RuleConditionDefinition> ruleConditions = new ArrayList<RuleConditionDefinition>(3);
        ruleConditions.add(new RuleConditionDefinitionImpl("no-condition", "All Content", 
                             "This condition will match any item of content added to the space. " +
                             "Use this when you wish to apply an action to everything when it is " +
                             "added to the space.", null));
        ruleConditions.add(new RuleConditionDefinitionImpl("in-category", "Content in a specific category", 
                             "The rule is applied to all content that has a specific category", null));
        ruleConditions.add(new RuleConditionDefinitionImpl("contains-text", "Content which contains " +
                             "specific text in its name", 
                             "The rule is applied to all content that has specific text in " +
                             "its name", null));
        return ruleConditions;
    }

    /**
     * @see org.alfresco.repo.rule.RuleService#getActionDefinitions()
     */
    public List<RuleActionDefinition> getActionDefinitions()
    {
        // TODO tempory result to aid UI development
        ArrayList<RuleActionDefinition> ruleActions = new ArrayList<RuleActionDefinition>(8);
        ruleActions.add(new RuleActionDefinitionImpl("simple-workflow", "Add simple workflow to content",
                          "This will add a simple workflow to the matched content. This " +
                          "will allow the content to be moved to a different space for " +
                          "its next step in a workflow.  You can also give a space for " +
                          "it to be moved to if you want a reject step", null));
        ruleActions.add(new RuleActionDefinitionImpl("link-category", "Link content to category",
                          "This will apply a category to the matched content.", null));
        ruleActions.add(new RuleActionDefinitionImpl("add-features", "Add features to content",
                          "This will add a feature to the matched content.", null));
        ruleActions.add(new RuleActionDefinitionImpl("copy", "Create a copy of content in a given format at " +
                          "a specific location",
                          "This will copy the matched content to another location with a" +
                          "specific format.", null));
        ruleActions.add(new RuleActionDefinitionImpl("move", "Move content to a specific space",
                          "This will move the matched content to another space.", null));
        ruleActions.add(new RuleActionDefinitionImpl("email", "Send an email to specified users",
                          "This will send an email to a list of users when the content matches.", null));
        ruleActions.add(new RuleActionDefinitionImpl("check-in", "Check in content",
                          "This will check in the matched content.", null));
        ruleActions.add(new RuleActionDefinitionImpl("check-out", "Check out content",
                          "This will check out the matched content.", null));
        
        return ruleActions;
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
