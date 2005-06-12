/**
 * Created on May 20, 2005
 */
package org.alfresco.repo.version.common;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.repo.dictionary.AssociationDefinition;
import org.alfresco.repo.dictionary.ClassDefinition;
import org.alfresco.repo.dictionary.DictionaryService;
import org.alfresco.repo.dictionary.NamespaceService;
import org.alfresco.repo.dictionary.PropertyDefinition;
import org.alfresco.repo.dictionary.impl.DictionaryBootstrap;
import org.alfresco.repo.node.NodeService;
import org.alfresco.repo.policy.ClassPolicyDelegate;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.policy.PolicyScope;
import org.alfresco.repo.ref.ChildAssocRef;
import org.alfresco.repo.ref.NodeAssocRef;
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
     * The dictionary service
     */
    protected DictionaryService dictionaryService;
	
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
     * Sets the dictionary service
     * 
     * @param dictionaryService  the dictionary service
     */
    public void setDictionaryService(DictionaryService dictionaryService)
    {
        this.dictionaryService = dictionaryService;
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
				QName.createQName(NamespaceService.ALFRESCO_URI, "onCopyNode"),
				DictionaryBootstrap.ASPECT_QNAME_VERSIONABLE,
				new JavaBehaviour(this, "onCopy"));
		
		// Register the onCreateVersion behavior for the version aspect
		this.policyComponent.bindClassBehaviour(
				QName.createQName(NamespaceService.ALFRESCO_URI, "onCreateVersion"),
				DictionaryBootstrap.ASPECT_QNAME_VERSIONABLE,
				new JavaBehaviour(this, "onCreateVersion"));
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
	public void onCopy(QName sourceClassRef, NodeRef sourceNodeRef, PolicyScope copyDetails)
	{
		// Add the version aspect, but do not copy any of the properties
		copyDetails.addAspect(DictionaryBootstrap.ASPECT_QNAME_VERSIONABLE);
	}
	
	/**
	 * OnCreateVersion behaviour for the version aspect
	 * <p>
	 * Ensures that the version aspect and it proerties are 'frozen' as part of
	 * the versioned state.
	 * 
	 * @param classRef				the class reference
	 * @param versionableNode		the versionable node reference
	 * @param versionProperties		the version properties
	 * @param nodeDetails			the details of the node to be versioned
	 */
	public void onCreateVersion(
			QName classRef,
			NodeRef versionableNode, 
			Map<String, Serializable> versionProperties,
			PolicyScope nodeDetails)
	{
		// Do nothing since we do not what to freeze any of the version 
		// properties
	}
	
	/**
	 * Invokes the before create version policy behaviour
	 * 
	 * @param nodeRef  the node being versioned
	 */
	protected void invokeBeforeCreateVersion(NodeRef nodeRef)
	{
        // invoke for node type
        QName nodeTypeQName = nodeService.getType(nodeRef);
        this.beforeCreateVersionDelegate.get(nodeTypeQName).beforeCreateVersion(nodeRef);
        // invoke for node aspects
        Set<QName> nodeAspectQNames = nodeService.getAspects(nodeRef);
		this.beforeCreateVersionDelegate.get(nodeAspectQNames).beforeCreateVersion(nodeRef);
	}
	
	/**
	 * Invoke the on create version policy behaviour
	 *
	 */
	protected void invokeOnCreateVersion(
			NodeRef nodeRef, 
			Map<String, Serializable> versionProperties, 
			PolicyScope nodeDetails)
	{
		// Sort out the policies for the node type
		QName classRef = this.nodeService.getType(nodeRef);
		invokeOnCreateVersion(classRef, nodeRef, versionProperties, nodeDetails);
		
		// Sort out the policies for the aspects
		Collection<QName> aspects = this.nodeService.getAspects(nodeRef);
		for (QName aspect : aspects) 
		{
			invokeOnCreateVersion(aspect, nodeRef, versionProperties, nodeDetails);
		}
		
	}
	
	/**
	 * Invokes the on create version policy behaviour for a given type 
	 * 
	 * @param classRef
	 * @param nodeDetails
	 * @param nodeRef
	 * @param versionProperties
	 */
	private void invokeOnCreateVersion(
			QName classRef,
			NodeRef nodeRef,
			Map<String, Serializable> versionProperties,
			PolicyScope nodeDetails)
	{
		Collection<OnCreateVersionPolicy> policies = this.onCreateVersionDelegate.getList(classRef);
		if (policies.size() == 0)
		{
			// Call the default implementation
			defaultOnCreateVersion(
					classRef,
					nodeRef,
					versionProperties,
					nodeDetails);
		}
		else
		{
			// Call the policy definitions
			for (VersionServicePolicies.OnCreateVersionPolicy policy : policies) 
			{
				policy.onCreateVersion(
						classRef,
						nodeRef,
						versionProperties,
						nodeDetails);
			}
		}
	}
	
	/**
	 * Default implementation of the on create version policy.  Called if no behaviour is registered for the 
	 * policy for the specified type.
	 * 
	 * @param nodeRef
	 * @param versionProperties
	 * @param nodeDetails
	 */
	protected void defaultOnCreateVersion(
			QName classRef,
			NodeRef nodeRef, 
			Map<String, Serializable> versionProperties, 
			PolicyScope nodeDetails)
	{
		ClassDefinition classDefinition = this.dictionaryService.getClass(classRef);	
		if (classDefinition != null)
		{			
			// Copy the properties
			Map<QName,PropertyDefinition> propertyDefinitions = classDefinition.getProperties();
			for (QName propertyName : propertyDefinitions.keySet()) 
			{
				Serializable propValue = this.nodeService.getProperty(nodeRef, propertyName);
				nodeDetails.addProperty(classRef, propertyName, propValue);
			}			
			
			// Copy the associations (child and target)
			Map<QName,AssociationDefinition> assocDefs = classDefinition.getAssociations();
			for (AssociationDefinition assocDef : assocDefs.values()) 
			{
				if (assocDef.isChild() == true)
				{
					List<ChildAssocRef> childAssocRefs = this.nodeService.getChildAssocs(nodeRef, assocDef.getName());
					for (ChildAssocRef childAssocRef : childAssocRefs) 
					{
						nodeDetails.addChildAssociation(classRef, assocDef.getName(), childAssocRef);
					}
				}
				else
				{
					List<NodeAssocRef> nodeAssocRefs = this.nodeService.getTargetAssocs(nodeRef, assocDef.getName());
					for (NodeAssocRef nodeAssocRef : nodeAssocRefs) 
					{
						nodeDetails.addAssociation(classRef, assocDef.getName(), nodeAssocRef);
					}
				}
			}
		}
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
			QName classRef,
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
			throw new VersionServiceException("More than one CalculateVersionLabelPolicy behaviour has been registered for the type " + classRef.toString());
		}
		
		return versionLabel;
	}
	 
}
