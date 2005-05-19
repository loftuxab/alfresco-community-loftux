/**
 * Created on Apr 27, 2005
 */
package org.alfresco.repo.version;

import org.alfresco.repo.dictionary.NamespaceService;
import org.alfresco.repo.policy.ClassPolicy;
import org.alfresco.repo.ref.NodeRef;

/**
 * Version service policy interfaces
 * 
 * @author Roy Wetherall
 */
public interface VersionServicePolicies
{
	/**
	 * 
	 */
	public interface BeforeCreateVersionPolicy extends ClassPolicy
	{
		/**
		 * Policy meta data
		 */
		static final String NAMESPACE = NamespaceService.ALFRESCO_URI;
		
		/**
		 * Called before a new version is created for a version
		 * 
		 * @param versionableNode  reference to the node about to be versioned
		 */
	    public void beforeCreateVersion(NodeRef versionableNode);
	
	}
	
	/**
	 * 
	 */
	public interface OnCreateVersionPolicy extends ClassPolicy
	{
		/**
		 * Policy meta data
		 */
		static final String NAMESPACE = NamespaceService.ALFRESCO_URI;
		
		/**
		 * 
		 * @param versionableNode
		 */
		public void onCreateVersion(NodeRef versionableNode);
	}
}
