package org.alfresco.repo.node;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.alfresco.repo.node.NodeServicePolicies.BeforeCreateNodePolicy;
import org.alfresco.repo.node.NodeServicePolicies.BeforeCreateStorePolicy;
import org.alfresco.repo.node.NodeServicePolicies.BeforeDeleteNodePolicy;
import org.alfresco.repo.node.NodeServicePolicies.BeforeUpdateNodePolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnCreateChildAssociationPolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnCreateNodePolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnCreateStorePolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnDeleteChildAssociationPolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnDeleteNodePolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnUpdateNodePolicy;
import org.alfresco.repo.policy.AssociationPolicyDelegate;
import org.alfresco.repo.policy.ClassPolicyDelegate;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.ref.ChildAssocRef;
import org.alfresco.repo.ref.NamespacePrefixResolver;
import org.alfresco.repo.ref.NodeRef;
import org.alfresco.repo.ref.QName;
import org.alfresco.repo.ref.StoreRef;
import org.alfresco.repo.ref.qname.QNamePattern;
import org.alfresco.repo.ref.qname.RegexQNamePattern;
import org.alfresco.repo.search.Indexer;
import org.alfresco.repo.search.QueryParameterDefinition;
import org.alfresco.repo.search.ResultSet;
import org.alfresco.repo.search.Searcher;
import org.alfresco.repo.search.impl.lucene.LuceneQueryParser;
import org.alfresco.util.debug.CodeMonkey;
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
	/** controls policy delegates */
	private PolicyComponent policyComponent;
    /** the component with which to index the node hierarchy (optional) */
    private Indexer indexer;
    /** the component with which to search the node hierarchy (optional) */
    private Searcher searcher;
	
	/**
	 * Policy delegates
	 */
    private ClassPolicyDelegate<BeforeCreateStorePolicy> beforeCreateStoreDelegate;
    private ClassPolicyDelegate<OnCreateStorePolicy> onCreateStoreDelegate;
    private ClassPolicyDelegate<BeforeCreateNodePolicy> beforeCreateNodeDelegate;
	private ClassPolicyDelegate<OnCreateNodePolicy> onCreateNodeDelegate;
	private ClassPolicyDelegate<BeforeUpdateNodePolicy> beforeUpdateNodeDelegate;
	private ClassPolicyDelegate<OnUpdateNodePolicy> onUpdateNodeDelegate;
	private ClassPolicyDelegate<BeforeDeleteNodePolicy> beforeDeleteNodeDelegate;
	private ClassPolicyDelegate<OnDeleteNodePolicy> onDeleteNodeDelegate;	
    private AssociationPolicyDelegate<OnCreateChildAssociationPolicy> onCreateChildAssociationDelegate;
    private AssociationPolicyDelegate<OnDeleteChildAssociationPolicy> onDeleteChildAssociationDelegate;
	
	/**
	 * @param policyComponent  the component with which to register class policies and behaviour
	 */
	protected AbstractNodeServiceImpl(PolicyComponent policyComponent)
	{
		this.policyComponent = policyComponent;
	}

    /**
     * Optionally set the indexer to index the node hierarchy
     * 
     * @param indexer the component used to index node hierarchy.  It is optional and if not
     *      supplied implies that the node hierarchy will not be searchable 
     */
    public void setIndexer(Indexer indexer)
    {
        this.indexer = indexer;
    }
    
    /**
     * Optionally set the searcher to enable the {@link #contains(NodeRef, QName, String) contains}
     * and {@link #like(NodeRef, QName, String) like} search functionality.
     * <p>
     * 
     * The searcher will be ignored if the {@link #setIndexer(Indexer) indexer} is not present
     * as there will never be any search results available without indexing.
     * 
     * @param searcher the component used to search the node hierarchy.  it is optional
     *      and if not supplied will disable the node search functionality. 
     */
    public void setSearcher(Searcher searcher)
    {
        this.searcher = searcher;
    }
	
	/**
	 * Registers the node policies as well as node indexing behaviour if the
     * {@link #setIndexer(Indexer) indexer} is present.
	 */
	public void init()
	{
		// Register the various policies
        beforeCreateStoreDelegate = policyComponent.registerClassPolicy(NodeServicePolicies.BeforeCreateStorePolicy.class);
        onCreateStoreDelegate = policyComponent.registerClassPolicy(NodeServicePolicies.OnCreateStorePolicy.class);
        beforeCreateNodeDelegate = policyComponent.registerClassPolicy(NodeServicePolicies.BeforeCreateNodePolicy.class);
		onCreateNodeDelegate = policyComponent.registerClassPolicy(NodeServicePolicies.OnCreateNodePolicy.class);
		beforeUpdateNodeDelegate = policyComponent.registerClassPolicy(NodeServicePolicies.BeforeUpdateNodePolicy.class);
		onUpdateNodeDelegate = policyComponent.registerClassPolicy(NodeServicePolicies.OnUpdateNodePolicy.class);
		beforeDeleteNodeDelegate = policyComponent.registerClassPolicy(NodeServicePolicies.BeforeDeleteNodePolicy.class);
		onDeleteNodeDelegate = policyComponent.registerClassPolicy(NodeServicePolicies.OnDeleteNodePolicy.class);
        onCreateChildAssociationDelegate = policyComponent.registerAssociationPolicy(NodeServicePolicies.OnCreateChildAssociationPolicy.class);
        onDeleteChildAssociationDelegate = policyComponent.registerAssociationPolicy(NodeServicePolicies.OnDeleteChildAssociationPolicy.class);
	}
	
    /**
     * Invoke behaviour to be called before a store is created
     * 
     * @see NodeServicePolicies.BeforeCreateStorePolicy#beforeCreateStore(QName, StoreRef)
     */
    protected void invokeBeforeCreateStore(QName nodeTypeQName, StoreRef storeRef)
    {
        NodeServicePolicies.BeforeCreateStorePolicy policy = this.beforeCreateStoreDelegate.get(nodeTypeQName);
        policy.beforeCreateStore(nodeTypeQName, storeRef);
    }
    
    /**
     * Invoke behaviour to be called after a store has been created
     * 
     * @see NodeServicePolicies.OnCreateStorePolicy#onCreateStore(NodeRef)
     */
    protected void invokeOnCreateStore(NodeRef nodeRef)
    {
        NodeServicePolicies.OnCreateStorePolicy policy = this.onCreateStoreDelegate.get(this, nodeRef);
        policy.onCreateStore(nodeRef);
    }
    
	/**
	 * Invoke the beforeCreate policy behaviour
	 * 
	 * @param parentRef			the parent node reference
	 * @param assocTypeQName	the association type
	 * @param assocQName		the association name
	 * @param nodeTypeQName		the node type
	 */
	protected void invokeBeforeCreateNode(
			NodeRef parentRef, 
			QName assocTypeQName, 
			QName assocQName, 
			QName nodeTypeQName)
	{
		NodeServicePolicies.BeforeCreateNodePolicy policy = this.beforeCreateNodeDelegate.get(nodeTypeQName);
		policy.beforeCreateNode(parentRef, assocTypeQName, assocQName, nodeTypeQName);
	}
	
	/**
	 * Invoke the onCreate policy behaviour
	 * 
	 * @param childAssocRef  the child assocation reference
	 */
	protected void invokeOnCreateNode(ChildAssocRef childAssocRef)
	{
		NodeRef nodeRef = childAssocRef.getChildRef();
		this.onCreateNodeDelegate.get(this, nodeRef).onCreateNode(childAssocRef);		
	}
	
	/**
	 * Invoke the beforeUpdate policy behaviour
	 * 
	 * @param nodeRef  the node reference
	 */
	protected void invokeBeforeUpdateNode(NodeRef nodeRef)
	{
		this.beforeUpdateNodeDelegate.get(this, nodeRef).beforeUpdateNode(nodeRef);
	}
	
	/**
	 * Invoke the onUpdate policy behaviour
	 * 
	 * @param nodeRef  the node reference
	 */
	protected void invokeOnUpdateNode(NodeRef nodeRef)
	{
		this.onUpdateNodeDelegate.get(this, nodeRef).onUpdateNode(nodeRef);
	}
	
	/**
	 * Invoke the beforeDelete policy behaviour
	 * 
	 * @param nodeRef  the node reference
	 */
	protected void invokeBeforeDeleteNode(NodeRef nodeRef)
	{
		this.beforeDeleteNodeDelegate.get(this, nodeRef).beforeDeleteNodePolicy(nodeRef);
	}
	
	/**
	 * Invoke the onDelete policy behaviour
	 * 
     * @see NodeServicePolicies.OnDeleteNodePolicy#onDeleteNode(ChildAssocRef)
	 */
	protected void invokeOnDeleteNode(QName nodeTypeQName, ChildAssocRef childAssocRef)
	{
		NodeServicePolicies.OnDeleteNodePolicy policy = this.onDeleteNodeDelegate.get(nodeTypeQName);
		policy.onDeleteNode(childAssocRef);		
	}
    
    /**
     * TODO: NOOP
     */
    protected void invokeBeforeCreateChildAssociation(ChildAssocRef childAssocRef)
    {
        // TODO
        CodeMonkey.todo("Create policies, etc");
    }

    /**
     * Invoke behaviour registered to listen for creation of child associations
     * 
     * @see NodeServicePolicies.OnCreateChildAssociationPolicy#onCreateChildAssociation(ChildAssocRef)
     */
    protected void invokeOnCreateChildAssociation(ChildAssocRef childAssocRef)
    {
        NodeServicePolicies.OnCreateChildAssociationPolicy policy = 
            this.onCreateChildAssociationDelegate.get(this, childAssocRef.getParentRef(), childAssocRef.getTypeQName());
        policy.onCreateChildAssociation(childAssocRef);
    }

    /**
     * TODO: NOOP
     */
    protected void invokeBeforeDeleteChildAssociation(ChildAssocRef childAssocRef)
    {
        // TODO
        CodeMonkey.todo("Create policies, etc");
    }
    
    /**
     * Invoke behaviour registered to listen for deletion of child associations
     * 
     * @see NodeServicePolicies.OnCreateChildAssociationPolicy#onCreateChildAssociation(ChildAssocRef)
     */
    protected void invokeOnDeleteChildAssociation(ChildAssocRef childAssocRef)
    {
        // TODO
        CodeMonkey.todo("The assoc qname being passed in must be the type qname not the assoc qname");
//        NodeServicePolicies.OnDeleteChildAssociationPolicy policy = 
//            this.onDeleteChildAssociationDelegate.get(this, childAssocRef.getParentRef(), childAssocRef.getQName());
//        policy.onDeleteChildAssociation(childAssocRef);
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

    public List<ChildAssocRef> selectNodes(
            NodeRef contextNodeRef,
            String xPath,
            QueryParameterDefinition[] paramDefs,
            NamespacePrefixResolver namespacePrefixResolver,
            boolean followAllParentLinks)
    {
        try
        {
            NodeServiceXPath xpath = new NodeServiceXPath(xPath, this, namespacePrefixResolver, paramDefs, followAllParentLinks);
            for(String prefix: namespacePrefixResolver.getPrefixes())
            {
                xpath.addNamespace(prefix, namespacePrefixResolver.getNamespaceURI(prefix));
            }
            List list = xpath.selectNodes(getPrimaryParent(contextNodeRef));
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

    public List<Serializable> selectProperties(
            NodeRef contextNodeRef,
            String xPath,
            QueryParameterDefinition[] paramDefs,
            NamespacePrefixResolver namespacePrefixResolver,
            boolean followAllParentLinks)
    {
        try
        {
            NodeServiceXPath xpath = new NodeServiceXPath(xPath, this, namespacePrefixResolver, paramDefs, followAllParentLinks);
            for(String prefix: namespacePrefixResolver.getPrefixes())
            {
                xpath.addNamespace(prefix, namespacePrefixResolver.getNamespaceURI(prefix));
            }
            List list = xpath.selectNodes(getPrimaryParent(contextNodeRef));
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

    /**
     * @return Returns true if the pattern is present, otherwise false.
     * 
     * @see #setIndexer(Indexer)
     * @see #setSearcher(Searcher)
     */
    public boolean contains(NodeRef nodeRef, QName propertyQName, String googleLikePattern)
    {
        if (searcher == null || indexer == null)
        {
            // without both, no Lucene search is possible
            return false;
        }
        // build Lucene search string specific to the node
        StringBuffer sb = new StringBuffer();
        sb.append("+ID:").append(nodeRef.getId()).append(" +(TEXT:(").append(googleLikePattern).append(") ");
        if (propertyQName != null)
        {
            sb.append("@").append(LuceneQueryParser.escape(propertyQName.toString()));
            sb.append(":(").append(googleLikePattern).append(")");
        }
        else
        {
            for (QName key : getProperties(nodeRef).keySet())
            {
                sb.append("@").append(LuceneQueryParser.escape(key.toString()));
                sb.append(":(").append(googleLikePattern).append(")");
            }
        }
        sb.append(")");

        ResultSet resultSet = searcher.query(nodeRef.getStoreRef(), "lucene", sb.toString());
        boolean answer = resultSet.length() > 0;
        return answer;
    }

    /**
     * @return Returns true if the pattern is present, otherwise false.
     * 
     * @see #setIndexer(Indexer)
     * @see #setSearcher(Searcher)
     */
    public boolean like(NodeRef nodeRef, QName propertyQName, String sqlLikePattern)
    {
        if (searcher == null || indexer == null)
        {
            // without both, no Lucene search is possible
            return false;
        }

        // Need to turn the SQL like patter into lucene line
        // Need to replace unescaped % with * (? and ? will match and \ is used
        // for escape so the rest is OK)

        // replace the SQL-like search string with appropriate Lucene syntax
        StringBuffer sb = new StringBuffer();
        char previous = ' ';
        char current = ' ';
        for (int i = 0; i < sqlLikePattern.length(); i++)
        {
            previous = current;
            current = sqlLikePattern.charAt(i);
            if ((current == '%') && (previous != '\\'))
            {
                sb.append("*");
            }
            else
            {
                sb.append(current);
            }
        }
        String pattern = sb.toString();

        // build Lucene search string specific to the node
        sb = new StringBuffer();
        sb.append("+ID:").append(nodeRef.getId()).append(" +(TEXT:(").append(pattern).append(") ");
        if (propertyQName != null)
        {
            sb.append("@").append(LuceneQueryParser.escape(propertyQName.toString())).append(":(").append(pattern).append(")");
        }
        sb.append(")");

        ResultSet resultSet = searcher.query(nodeRef.getStoreRef(), "lucene", sb.toString());
        boolean answer = resultSet.length() > 0;
        return answer;
    }

    protected Indexer getIndexer()
    {
        return indexer;
    }
    

    protected Searcher getSearcher()
    {
        return searcher;
    }
    
    
    
}
