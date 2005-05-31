package org.alfresco.repo.node;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.alfresco.repo.dictionary.ClassRef;
import org.alfresco.repo.node.NodeServicePolicies.BeforeCreatePolicy;
import org.alfresco.repo.node.NodeServicePolicies.BeforeDeletePolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnCreatePolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnDeletePolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnUpdatePolicy;
import org.alfresco.repo.policy.ClassPolicyDelegate;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.ref.ChildAssocRef;
import org.alfresco.repo.ref.NamespacePrefixResolver;
import org.alfresco.repo.ref.NodeRef;
import org.alfresco.repo.ref.QName;
import org.alfresco.repo.ref.qname.QNamePattern;
import org.alfresco.repo.ref.qname.RegexQNamePattern;
import org.alfresco.repo.search.QueryParameterDefinition;
import org.jaxen.JaxenException;

/**
 * Provides common functionality for {@link org.alfresco.repo.node.NodeService}
 * implementations.
 * <p>
 * Some of the overloaded simpler versions of methods are implemented by passing
 * through the defaults as required.
 * <p>
 * The callback handling is also provided as a convenience for implementations.
 * 
 * @author Derek Hulley
 */
public abstract class AbstractNodeServiceImpl implements NodeService
{
	/**
	 * The policy component
	 */
	protected PolicyComponent policyComponent;
	
	/**
	 * Policy delegates
	 */
	private ClassPolicyDelegate<BeforeCreatePolicy> beforeCreateDelegate;
	private ClassPolicyDelegate<OnCreatePolicy> onCreateDelegate;
	private ClassPolicyDelegate<BeforeUpdatePolicy> beforeUpdateDelegate;
	private ClassPolicyDelegate<OnUpdatePolicy> onUpdateDelegate;
	private ClassPolicyDelegate<BeforeDeletePolicy> beforeDeleteDelegate;
	private ClassPolicyDelegate<OnDeletePolicy> onDeleteDelegate;	
	
	/**
	 * Constructor
	 * 
	 * @param policyComponent  the policy component
	 */
	protected AbstractNodeServiceImpl(PolicyComponent policyComponent)
	{
		this.policyComponent = policyComponent;
	}
	
	/**
	 * Initialise method	 
	 */
	public void init()
	{
		// Register the various policies
		this.beforeCreateDelegate = this.policyComponent.registerClassPolicy(NodeServicePolicies.BeforeCreatePolicy.class);
		this.onCreateDelegate = this.policyComponent.registerClassPolicy(NodeServicePolicies.OnCreatePolicy.class);
		this.beforeUpdateDelegate = this.policyComponent.registerClassPolicy(BeforeUpdatePolicy.class);
		this.onUpdateDelegate = this.policyComponent.registerClassPolicy(NodeServicePolicies.OnUpdatePolicy.class);
		this.beforeDeleteDelegate = this.policyComponent.registerClassPolicy(NodeServicePolicies.BeforeDeletePolicy.class);
		this.onDeleteDelegate = this.policyComponent.registerClassPolicy(NodeServicePolicies.OnDeletePolicy.class);
	}
	
	/**
	 * Invoke the beforeCreate policy behaviour
	 * 
	 * @param parentRef			the parent node reference
	 * @param assocTypeQName	the association type
	 * @param assocQName		the association name
	 * @param nodeTypeQName		the node type
	 */
	protected void invokeBeforeCreate(
			NodeRef parentRef, 
			QName assocTypeQName, 
			QName assocQName, 
			QName nodeTypeQName)
	{
		NodeServicePolicies.BeforeCreatePolicy policy = this.beforeCreateDelegate.get(new ClassRef(nodeTypeQName));
		policy.beforeCreate(parentRef, assocTypeQName, assocQName, nodeTypeQName);
	}
	
	/**
	 * Invoke the onCreate policy behaviour
	 * 
	 * @param childAssocRef  the child assocation reference
	 */
	protected void invokeOnCreate(ChildAssocRef childAssocRef)
	{
		NodeRef nodeRef = childAssocRef.getChildRef();
		this.onCreateDelegate.get(this, nodeRef).onCreate(childAssocRef);		
	}
	
	/**
	 * Invoke the beforeUpdate policy behaviour
	 * 
	 * @param nodeRef  the node reference
	 */
	protected void invokeBeforeUpdate(NodeRef nodeRef)
	{
		this.beforeUpdateDelegate.get(this, nodeRef).beforeUpdate(nodeRef);
	}
	
	/**
	 * Invoke the onUpdate policy behaviour
	 * 
	 * @param nodeRef  the node reference
	 */
	protected void invokeOnUpdate(NodeRef nodeRef)
	{
		this.onUpdateDelegate.get(this, nodeRef).onUpdate(nodeRef);
	}
	
	/**
	 * Invoke the beforeDelete policy behaviour
	 * 
	 * @param nodeRef  the node reference
	 */
	protected void invokeBeforeDelete(NodeRef nodeRef)
	{
		this.beforeDeleteDelegate.get(this, nodeRef).beforeDeletePolicy(nodeRef);
	}
	
	/**
	 * Invoke the onDelete policy behaviour
	 * 
	 * @param nodeRef  the node reference
	 */
	protected void invokeOnDelete(QName typeQName, NodeRef nodeRef)
	{
		ClassRef classRef = new ClassRef(typeQName);
		NodeServicePolicies.OnDeletePolicy policy = this.onDeleteDelegate.get(classRef);
		policy.onDelete(classRef, nodeRef);		
	}
    
    /**
     * Defers to the pattern matching overload
     * 
     * @see RegexQNamePattern#MATCH_ALL
     * @see NodeService#getParentAssocs(NodeRef, QNamePattern)
     */
    public List<ChildAssocRef> getParentAssocs(NodeRef nodeRef) throws InvalidNodeRefException
    {
        return getParentAssocs(nodeRef, RegexQNamePattern.MATCH_ALL);
    }

    /**
     * Defers to the pattern matching overload
     * 
     * @see RegexQNamePattern#MATCH_ALL
     * @see NodeService#getChildAssocs(NodeRef, QNamePattern)
     */
    public final List<ChildAssocRef> getChildAssocs(NodeRef nodeRef) throws InvalidNodeRefException
    {
        return getChildAssocs(nodeRef, RegexQNamePattern.MATCH_ALL);
    }

    public List<ChildAssocRef> selectNodes(NodeRef contextNode, String xPath, QueryParameterDefinition[] paramDefs, NamespacePrefixResolver namespacePrefixResolver, boolean followAllParentLinks) throws XPathException
    {
        try
        {
           NodeServiceXPath xpath = new NodeServiceXPath(xPath, this, namespacePrefixResolver, paramDefs, followAllParentLinks);
           for(String prefix: namespacePrefixResolver.getPrefixes())
           {
              xpath.addNamespace(prefix, namespacePrefixResolver.getNamespaceURI(prefix));
           }
           List list = xpath.selectNodes(getPrimaryParent(contextNode));
           List<ChildAssocRef> answer = new ArrayList<ChildAssocRef>(list.size());
           for(Object o: list)
           {
               if(!(o instanceof ChildAssocRef))
               {
                   throw new XPathException("Xpath expression must only select nodes");
               }
               answer.add((ChildAssocRef)o);
           }
           return answer;
        }
        catch(JaxenException e)
        {
            throw new XPathException("Error executing xpath", e);
        }
    }

    public List<Serializable> selectProperties(NodeRef contextNode, String xPath, QueryParameterDefinition[] paramDefs, NamespacePrefixResolver namespacePrefixResolver, boolean followAllParentLinks)
    {
        try
        {
           NodeServiceXPath xpath = new NodeServiceXPath(xPath, this, namespacePrefixResolver, paramDefs, followAllParentLinks);
           for(String prefix: namespacePrefixResolver.getPrefixes())
           {
              xpath.addNamespace(prefix, namespacePrefixResolver.getNamespaceURI(prefix));
           }
           List list = xpath.selectNodes(getPrimaryParent(contextNode));
           List<Serializable> answer = new ArrayList<Serializable>(list.size());
           for(Object o: list)
           {
               if(!(o instanceof DocumentNavigator.Property))
               {
                   throw new XPathException("Xpath expression must only select nodes");
               }
               answer.add(((DocumentNavigator.Property)o).value);
           }
           return answer;
        }
        catch(JaxenException e)
        {
            throw new XPathException("Error executing xpath", e);
        }
    }

    public boolean contains(NodeRef nodeRef, QName property, String googleLikePattern)
    {
        return false;
    }

    public boolean like(NodeRef nodeRef, QName property, String sqlLikePattern)
    {
        return false;
    }
    
    
}
