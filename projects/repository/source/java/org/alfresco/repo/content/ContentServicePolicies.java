package org.alfresco.repo.content;

import org.alfresco.repo.policy.ClassPolicy;
import org.alfresco.service.cmr.repository.NodeRef;

/**
 * Content service policies interface
 * 
 * @author Roy Wetherall
 */
public interface ContentServicePolicies
{
	/**
	 * On content update policy interface
	 */
	public interface OnContentUpdatePolicy extends ClassPolicy
	{
		/**
		 * @param nodeRef	the node reference
		 */
		public void onContentUpdate(NodeRef nodeRef);
	}
}
