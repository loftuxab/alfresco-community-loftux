package org.alfresco.repo.node;

import org.alfresco.repo.dictionary.NamespaceService;
import org.alfresco.repo.policy.ClassPolicy;
import org.alfresco.repo.ref.NodeRef;

/**
 * BeforeUpdate policy interface
 */
public interface BeforeUpdatePolicy extends ClassPolicy
{
	/**
	 * Policy meta data
	 */
	static final String NAMESPACE = NamespaceService.ALFRESCO_URI;

	/**
	 * Called before a node is updated.  This includes the modification of properties, child and target 
	 * associations and the addition of aspects.
	 * 
	 * @param nodeRef  reference to the node being updated
	 */
	public void beforeUpdate(NodeRef nodeRef);
}