/**
 * Created on May 20, 2005
 */
package org.alfresco.repo.version.common;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

import org.alfresco.repo.dictionary.ClassRef;
import org.alfresco.repo.dictionary.NamespaceService;
import org.alfresco.repo.dictionary.bootstrap.DictionaryBootstrap;
import org.alfresco.repo.node.NodeService;
import org.alfresco.repo.node.operations.impl.NodeOperationsServiceImpl.CopyDetails;
import org.alfresco.repo.policy.ClassPolicyDelegate;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.ref.NodeRef;
import org.alfresco.repo.ref.QName;
import org.alfresco.repo.version.Version;
import org.alfresco.repo.version.VersionServiceException;
import org.alfresco.repo.version.VersionServicePolicies;
import org.alfresco.repo.version.VersionServicePolicies.BeforeCreateVersionPolicy;
import org.alfresco.repo.version.VersionServicePolicies.CalculateVersionLabelPolicy;
import org.alfresco.repo.version.VersionServicePolicies.OnCreateVersionPolicy;

/**
 * Abstract version service implementation.
 * 
 * @author Roy Wetherall
 */
public abstract class AbstractVersionServiceImpl 
{    
	/**
     * The common node service
     */
    protected NodeService nodeService ;
	
    /**
     * Policy component
     */
	protected PolicyComponent policyComponent;
	
	/**
	 * Policy delegates
	 */
	private ClassPolicyDelegate<BeforeCreateVersionPolicy> beforeCreateVersionDelegate;
	private ClassPolicyDelegate<OnCreateVersionPolicy> onCreateVersionDelegate;
	private ClassPolicyDelegate<CalculateVersionLabelPolicy> calculateVersionLabelDelegate;
    
	/**
     * Sets the general node service
     * 
     * @param nodeService   the node service
     */
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }
	
	/**
	 * Sets the policy component
	 * 
	 * @param policyComponent  the policy component
	 */
    public void setPolicyComponent(PolicyComponent policyComponent) 
	{
		this.policyComponent = policyComponent;
	}
	
	/**
	 * Initialise method
	 */
	public void initialise()
    {
		// Register the policies
        this.beforeCreateVersionDelegate = this.policyComponent.registerClassPolicy(VersionServicePolicies.BeforeCreateVersionPolicy.class);
		this.onCreateVersionDelegate = this.policyComponent.registerClassPolicy(VersionServicePolicies.OnCreateVersionPolicy.class);
		this.calculateVersionLabelDelegate = this.policyComponent.registerClassPolicy(VersionServicePolicies.CalculateVersionLabelPolicy.class);
		
		// Register the copy behaviour
		this.policyComponent.bindClassBehaviour(
				QName.createQName(NamespaceService.ALFRESCO_URI, "onCopy"),
				DictionaryBootstrap.ASPECT_CLASS_REF_VERSION,
				new JavaBehaviour(this, "onCopy"));
    }
	
	/**
	 * OnCopy behaviour implementation for the version aspect.
	 * <p>
	 * Ensures that the propety values of the version aspect are not copied onto
	 * the destination node.
	 * 
	 * @param sourceClassRef  the source class reference
	 * @param sourceNodeRef	  the source node reference
	 * @param copyDetails	  the copy details
	 */
	public void onCopy(ClassRef sourceClassRef, NodeRef sourceNodeRef, CopyDetails copyDetails)
	{
		// Add the version aspect, but do not copy any of the properties
		copyDetails.addAspect(DictionaryBootstrap.ASPECT_CLASS_REF_VERSION);
	}
	
	/**
	 * Invokes the before create version policy behaviour
	 * 
	 * @param nodeRef  the node being versioned
	 */
	protected void invokeBeforeCreateVersion(NodeRef nodeRef)
	{
		this.beforeCreateVersionDelegate.get(this.nodeService, nodeRef).beforeCreateVersion(nodeRef);
	}
	
	protected void invokeOnCreate()
	{
	}
	
	/**
	 * Invoke the calculate version label policy behaviour
	 * 
	 * @param classRef
	 * @param preceedingVersion
	 * @param versionNumber
	 * @param versionProperties
	 * @return
	 */
	protected String invokeCalculateVersionLabel(
			ClassRef classRef,
			Version preceedingVersion, 
			int versionNumber, 
			Map<String, Serializable>versionProperties)
	{
		String versionLabel = null;
		
		Collection<CalculateVersionLabelPolicy> behaviours = this.calculateVersionLabelDelegate.getList(classRef);
		if (behaviours.size() == 0)
		{
			// Default the version label to the version numbder
			versionLabel = Integer.toString(versionNumber);
		}
		else if (behaviours.size() == 1)
		{
			// Call the policy behaviour
			CalculateVersionLabelPolicy[] arr = behaviours.toArray(new CalculateVersionLabelPolicy[]{});
			versionLabel = arr[0].calculateVersionLabel(classRef, preceedingVersion, versionNumber, versionProperties);
		}
		else
		{
			// Error since we can only deal with a single caculate version label policy
			throw new VersionServiceException("More than one CalculateVersionLabelPolicy behaviour has been registered for the type " + classRef.getQName().toString());
		}
		
		return versionLabel;
	}
	 
}
