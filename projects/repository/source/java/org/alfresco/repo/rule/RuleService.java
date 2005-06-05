/**
 * Created on May 25, 2005
 */
package org.alfresco.repo.rule;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.alfresco.repo.ref.NodeRef;
import org.alfresco.repo.rule.impl.RuleImpl;

/**
 * Rule service interface.
 * 
 * @author Roy Wetherall
 */
public interface RuleService 
{
	/**
	 * Get the rule types currently defined in the repository.
	 * 
	 * @return	a list of rule types
	 */
	public List<RuleType> getRuleTypes();
	
	/**
	 * Get the condition definitions currently defined in the repository.
	 * 
	 * @return	a list of condition definitions
	 */
	public List<RuleConditionDefinition> getConditionDefinitions();
	
	/**
	 * Get the action definitions currently defined in the repository.
	 * 
	 * @return	a list of action definitions
	 */
	public List<RuleActionDefinition> getActionDefinitions();
	
	/**
	 * Makes a specified node Actionable.
	 * 
	 * @param nodeRef						the node reference
	 * @param ruleDefinitionFolderParent 	the node in which to create the rule defintion folder
	 */
	public void makeActionable(NodeRef nodeRef, NodeRef ruleDefinitionFolderParent);
	
	/**
	 * Indicates whether a node is actionable.
	 * 
	 * @param nodeRef   the node reference
	 * @return			true if the node is actionable, false otherwise
	 */
	public boolean isActionable(NodeRef nodeRef);
	
	/**
	 * Get all the rules associated with an actionable node, including those
	 * inherited from parents.
	 * <p>
	 * An exception is raised if the actionable aspect is not present on the 
	 * passed node.
	 * 
	 * @param nodeRef	the node reference
	 * @return			a list of the rules associated with the node 
	 */
	public List<RuleImpl> getRules(NodeRef nodeRef);
	
	/**
	 * Get the rules associated with an actionable node.
	 * <p>
	 * Optionally this list includes rules inherited from its parents.
	 * <p>
	 * An exception is raised if the actionable aspect is not present on the 
	 * passed node.
	 * 
	 * @param nodeRef			the node reference
	 * @param includeInhertied  indicates whether the inherited rules should be included
	 * 						    in the result list or not
	 * @return					a list of the rules associated with the node 
	 */
	public List<RuleImpl> getRules(NodeRef nodeRef, boolean includeInhertied);
	
	/**
	 * Get the rules associated with an actionable node that would be executed for a 
	 * specified execution context.
	 * <p>
	 * An exception is raised if the actionable aspect is not present on the 
	 * passed node.
	 * 
	 * @param nodeRef			the node reference
	 * @param ruleType			the type of rule to be executed
	 * @param executionContext	the execution context
	 * @return					a list of the rules that would be executed
	 */
	public List<RuleImpl> previewExecutingRules(NodeRef nodeRef, RuleType ruleType, Map<String, Serializable> executionContext);	
	
	/**
     * Helper method to create a new rule.
     * <p>
     * Call add rule once the details of the rule have been specified in
     * order to associate the rule with a node reference.
     * 
     * @param   ruleType    the type of rule to create
     * @return              the created rule
	 */
	public Rule createRule(RuleType ruleType);
	
	/**
	 * Adds the details of the rule to the specified node reference.
     * <p>
     * If the rule is already associated with the node, the details are 
     * updated with those specified.
	 * 
	 * @param nodeRef
	 * @param rule
	 */
	public void addRule(NodeRef nodeRef, Rule rule);
	
	/**
	 * Removes a rule associated with a node
	 * 
	 * @param nodeRef
	 * @param rule
	 */
	public void removeRule(NodeRef nodeRef, Rule rule);	
	
}
