package org.alfresco.repo.rule;

import java.util.List;

import org.alfresco.repo.rule.common.RuleImpl;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.rule.Rule;
import org.alfresco.service.cmr.rule.RuleType;

/**
 * Rule store interface
 * 
 * @author Roy Wetherall
 */
public interface RuleStore
{

    /**
     * Indicates whether the node has any rules associatied with it
     * 
     * @param nodeRef
     *            the node reference
     * @return true if it has rules associatied with it, false otherwise
     */
    boolean hasRules(NodeRef nodeRef);

    /**
     * Returns the rules by type for a node reference
     * 
     * @param nodeRef
     *            the node reference
     * @param ruleType
     *            the rule type
     * @return the rules
     */
    List<? extends Rule> getByRuleType(NodeRef nodeRef, RuleType ruleType);

    /**
     * Get a list of rules from the store.
     * 
     * @param nodeRef
     *            the node reference
     * @param includeInherited
     *            true if list includes inherited rules, false otherwise
     * @return a list of rules
     */
    List<? extends Rule> get(NodeRef nodeRef, boolean includeInherited);

    /**
     * Get the rule for a node that corresponds to the provided id.
     * 
     * @param nodeRef
     *            the node reference
     * @param ruleId
     *            the rule id
     * @return the rule, null if none found
     */
    Rule getById(NodeRef nodeRef, String ruleId);

    /**
     * Puts a rule into the store, updating or creating the rule store node
     * 
     * @param nodeRef
     *            the node reference
     * @param rule
     *            the rule
     */
    void put(NodeRef nodeRef, RuleImpl rule);

    /**
     * Removes a node from the store
     * 
     * @param nodeRef
     *            the node reference
     * @param rule
     *            the rule
     */
    void remove(NodeRef nodeRef, RuleImpl rule);

}