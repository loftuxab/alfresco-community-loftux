/**
 * Created on May 10, 2005
 */
package org.alfresco.repo.node.operations;

import org.alfresco.repo.policy.ClassPolicy;
import org.alfresco.repo.policy.PolicyScope;
import org.alfresco.repo.ref.NodeRef;
import org.alfresco.repo.ref.QName;

/**
 * @author Roy Wetherall
 */
public interface NodeOperationsServicePolicies 
{
	/**
	 * Policy invoked when a <b>node</b> is copied
	 */
	public interface OnCopyNodePolicy extends ClassPolicy
	{
        /**
         * 
         * @param classRef the type of node being copied
         * @param sourceNodeRef node being copied
         * @param copyDetails modifiable <b>node</b> details
         */
		public void onCopyNode(
				QName classRef,
				NodeRef sourceNodeRef,
				PolicyScope copyDetails);
	}
}
