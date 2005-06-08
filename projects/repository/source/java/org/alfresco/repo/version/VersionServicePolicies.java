/**
 * Created on Apr 27, 2005
 */
package org.alfresco.repo.version;

import java.io.Serializable;
import java.util.Map;

import org.alfresco.repo.policy.ClassPolicy;
import org.alfresco.repo.policy.PolicyScope;
import org.alfresco.repo.ref.NodeRef;
import org.alfresco.repo.ref.QName;

/**
 * Version service policy interfaces
 * 
 * @author Roy Wetherall
 */
public interface VersionServicePolicies
{
	/**
	 * Before create version policy interface.
	 */
	public interface BeforeCreateVersionPolicy extends ClassPolicy
	{
		/**
		 * Called before a new version is created for a version
		 * 
		 * @param versionableNode  reference to the node about to be versioned
		 */
	    public void beforeCreateVersion(NodeRef versionableNode);
	
	}
	
	/**
	 * On create version policy interface
	 */
	public interface OnCreateVersionPolicy extends ClassPolicy
	{
		public void onCreateVersion(
				QName classRef,
				NodeRef versionableNode, 
				Map<String, Serializable> versionProperties,
				PolicyScope nodeDetails);
	}
	
	/**
	 * Calculate version lable policy interface
	 */
	public interface CalculateVersionLabelPolicy extends ClassPolicy
	{
		public String calculateVersionLabel(
				QName classRef,
				Version preceedingVersion,
				int versionNumber,
				Map<String, Serializable>verisonProperties);
	}
}
