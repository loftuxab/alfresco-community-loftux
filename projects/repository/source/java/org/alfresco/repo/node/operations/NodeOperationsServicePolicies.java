/**
 * Created on May 10, 2005
 */
package org.alfresco.repo.node.operations;

import org.alfresco.repo.dictionary.NamespaceService;
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
	 * 
	 */
	public interface OnCopyPolicy extends ClassPolicy
	{
		/**
		 * Namespace 
		 */
		static final String NAMESPACE = NamespaceService.ALFRESCO_URI;
		
		/**
		 * 
		 */
		public void onCopy(
				QName classRef,
				NodeRef sourceNodeRef,
				PolicyScope copyDetails);
	}
}
