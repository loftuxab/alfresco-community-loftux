package org.alfresco.repo.node;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.alfresco.repo.node.NodeServicePolicies.BeforeAddAspectPolicy;
import org.alfresco.repo.node.NodeServicePolicies.BeforeCreateAssociationPolicy;
import org.alfresco.repo.node.NodeServicePolicies.BeforeCreateChildAssociationPolicy;
import org.alfresco.repo.node.NodeServicePolicies.BeforeCreateNodePolicy;
import org.alfresco.repo.node.NodeServicePolicies.BeforeCreateStorePolicy;
import org.alfresco.repo.node.NodeServicePolicies.BeforeDeleteAssociationPolicy;
import org.alfresco.repo.node.NodeServicePolicies.BeforeDeleteChildAssociationPolicy;
import org.alfresco.repo.node.NodeServicePolicies.BeforeDeleteNodePolicy;
import org.alfresco.repo.node.NodeServicePolicies.BeforeRemoveAspectPolicy;
import org.alfresco.repo.node.NodeServicePolicies.BeforeUpdateNodePolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnAddAspectPolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnCreateChildAssociationPolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnCreateNodePolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnCreateStorePolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnDeleteChildAssociationPolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnDeleteNodePolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnRemoveAspectPolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnUpdateNodePolicy;
import org.alfresco.repo.policy.AssociationPolicyDelegate;
import org.alfresco.repo.policy.ClassPolicyDelegate;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.search.Indexer;
import org.alfresco.repo.search.impl.lucene.LuceneQueryParser;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.InvalidNodeRefException;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.repository.XPathException;
import org.alfresco.service.cmr.search.QueryParameterDefinition;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.QNamePattern;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.jaxen.JaxenException;

/**
 * Provides common functionality for {@link org.alfresco.service.cmr.repository.NodeService}
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
    /** model reference */
    private DictionaryService dictionaryService;
    /** the component with which to index the node hierarchy (optional) */
    private Indexer indexer;
    /** the component with which to search the node hierarchy (optional) */
    private SearchService searcher;
	
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
    
    private ClassPolicyDelegate<BeforeAddAspectPolicy> beforeAddAspectDelegate;   
    private ClassPolicyDelegate<OnAddAspectPolicy> onAddAspectDelegate;   
    private ClassPolicyDelegate<BeforeRemoveAspectPolicy> beforeRemoveAspectDelegate;   
    private ClassPolicyDelegate<OnRemoveAspectPolicy> onRemoveAspectDelegate;
    
    private AssociationPolicyDelegate<BeforeCreateChildAssociationPolicy> beforeCreateChildAssociationDelegate;
    private AssociationPolicyDelegate<OnCreateChildAssociationPolicy> onCreateChildAssociationDelegate;
    private AssociationPolicyDelegate<BeforeDeleteChildAssociationPolicy> beforeDeleteChildAssociationDelegate;
    private AssociationPolicyDelegate<OnDeleteChildAssociationPolicy> onDeleteChildAssociationDelegate;
	
    private AssociationPolicyDelegate<BeforeCreateAssociationPolicy> beforeCreateAssociationDelegate;
    private AssociationPolicyDelegate<BeforeDeleteAssociationPolicy> beforeDeleteAssociationDelegate;

    /**
	 * @param policyComponent  the component with which to register class policies and behaviour
     * @param dictionaryService used to check that node operations conform to the model
	 */
	protected AbstractNodeServiceImpl(PolicyComponent policyComponent, DictionaryService dictionaryService)
	{
		this.policyComponent = policyComponent;
        this.dictionaryService = dictionaryService;
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
    public void setSearcher(SearchService searcher)
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
        
        beforeAddAspectDelegate = policyComponent.registerClassPolicy(NodeServicePolicies.BeforeAddAspectPolicy.class);
        onAddAspectDelegate = policyComponent.registerClassPolicy(NodeServicePolicies.OnAddAspectPolicy.class);
        beforeRemoveAspectDelegate = policyComponent.registerClassPolicy(NodeServicePolicies.BeforeRemoveAspectPolicy.class);
        onRemoveAspectDelegate = policyComponent.registerClassPolicy(NodeServicePolicies.OnRemoveAspectPolicy.class);
        
        beforeCreateChildAssociationDelegate = policyComponent.registerAssociationPolicy(NodeServicePolicies.BeforeCreateChildAssociationPolicy.class);
        onCreateChildAssociationDelegate = policyComponent.registerAssociationPolicy(NodeServicePolicies.OnCreateChildAssociationPolicy.class);
        beforeDeleteChildAssociationDelegate = policyComponent.registerAssociationPolicy(NodeServicePolicies.BeforeDeleteChildAssociationPolicy.class);
        onDeleteChildAssociationDelegate = policyComponent.registerAssociationPolicy(NodeServicePolicies.OnDeleteChildAssociationPolicy.class);

        beforeCreateAssociationDelegate = policyComponent.registerAssociationPolicy(NodeServicePolicies.BeforeCreateAssociationPolicy.class);
        beforeDeleteAssociationDelegate = policyComponent.registerAssociationPolicy(NodeServicePolicies.BeforeDeleteAssociationPolicy.class);
    }
	
    /**
     * @see NodeServicePolicies.BeforeCreateStorePolicy#beforeCreateStore(QName, StoreRef)
     */
    protected void invokeBeforeCreateStore(QName nodeTypeQName, StoreRef storeRef)
    {
        NodeServicePolicies.BeforeCreateStorePolicy policy = this.beforeCreateStoreDelegate.get(nodeTypeQName);
        policy.beforeCreateStore(nodeTypeQName, storeRef);
    }
    
    /**
     * @see NodeServicePolicies.OnCreateStorePolicy#onCreateStore(NodeRef)
     */
    protected void invokeOnCreateStore(NodeRef rootNodeRef)
    {
        // get qnames to invoke against
        Set<QName> qnames = getTypeAndAspectQNames(rootNodeRef);
        // execute policy for node type and aspects
        NodeServicePolicies.OnCreateStorePolicy policy = onCreateStoreDelegate.get(qnames);
        policy.onCreateStore(rootNodeRef);
    }
    
	/**
     * @see NodeServicePolicies.BeforeCreateNodePolicy#beforeCreateNode(NodeRef, QName, QName, QName)
	 */
	protected void invokeBeforeCreateNode(
			NodeRef parentNodeRef,
			QName assocTypeQName, 
			QName assocQName, 
			QName childNodeTypeQName)
	{
        // execute policy for node type
        NodeServicePolicies.BeforeCreateNodePolicy policy = beforeCreateNodeDelegate.get(childNodeTypeQName);
        policy.beforeCreateNode(parentNodeRef, assocTypeQName, assocQName, childNodeTypeQName);
	}
	
	/**
     * @see NodeServicePolicies.OnCreateNodePolicy#onCreateNode(ChildAssociationRef)
	 */
	protected void invokeOnCreateNode(ChildAssociationRef childAssocRef)
	{
        NodeRef childNodeRef = childAssocRef.getChildRef();
        // get qnames to invoke against
        Set<QName> qnames = getTypeAndAspectQNames(childNodeRef);
        // execute policy for node type and aspects
        NodeServicePolicies.OnCreateNodePolicy policy = onCreateNodeDelegate.get(qnames);
        policy.onCreateNode(childAssocRef);
	}
	
	/**
     * @see NodeServicePolicies.BeforeUpdateNodePolicy#beforeUpdateNode(NodeRef)
	 */
	protected void invokeBeforeUpdateNode(NodeRef nodeRef)
	{
        // get qnames to invoke against
        Set<QName> qnames = getTypeAndAspectQNames(nodeRef);
        // execute policy for node type and aspects
        NodeServicePolicies.BeforeUpdateNodePolicy policy = beforeUpdateNodeDelegate.get(qnames);
        policy.beforeUpdateNode(nodeRef);
	}
	
	/**
     * @see NodeServicePolicies.OnUpdateNodePolicy#onUpdateNode(NodeRef) 
	 */
	protected void invokeOnUpdateNode(NodeRef nodeRef)
	{
        // get qnames to invoke against
        Set<QName> qnames = getTypeAndAspectQNames(nodeRef);
        // execute policy for node type and aspects
        NodeServicePolicies.OnUpdateNodePolicy policy = onUpdateNodeDelegate.get(qnames);
        policy.onUpdateNode(nodeRef);
	}
	
	/**
     * @see NodeServicePolicies.BeforeDeleteNodePolicy#beforeDeleteNode(NodeRef)
	 */
	protected void invokeBeforeDeleteNode(NodeRef nodeRef)
	{
        // get qnames to invoke against
        Set<QName> qnames = getTypeAndAspectQNames(nodeRef);
        // execute policy for node type and aspects
        NodeServicePolicies.BeforeDeleteNodePolicy policy = beforeDeleteNodeDelegate.get(qnames);
        policy.beforeDeleteNode(nodeRef);
	}
	
	/**
     * @see NodeServicePolicies.OnDeleteNodePolicy#onDeleteNode(ChildAssociationRef)
	 */
	protected void invokeOnDeleteNode(
            ChildAssociationRef childAssocRef,
            QName childNodeTypeQName,
            Set<QName> childAspectQnames)
	{
        NodeRef parentNodeRef = childAssocRef.getParentRef();
        // get qnames to invoke against
        Set<QName> qnames = getTypeAndAspectQNames(parentNodeRef);
        // execute policy for node type and aspects
        NodeServicePolicies.OnDeleteNodePolicy policy = onDeleteNodeDelegate.get(qnames);
        policy.onDeleteNode(childAssocRef);
	}
    
    /**
     * @see NodeServicePolicies.BeforeAddAspectPolicy#beforeAddAspect(NodeRef, QName)
     */
    protected void invokeBeforeAddAspect(NodeRef nodeRef, QName aspectTypeQName)
    {
        NodeServicePolicies.BeforeAddAspectPolicy policy = beforeAddAspectDelegate.get(aspectTypeQName);
        policy.beforeAddAspect(nodeRef, aspectTypeQName);
    }
    
    /**
     * @see NodeServicePolicies.OnAddAspectPolicy#onAddAspect(NodeRef, QName)
     */
    protected void invokeOnAddAspect(NodeRef nodeRef, QName aspectTypeQName)
    {
        NodeServicePolicies.OnAddAspectPolicy policy = onAddAspectDelegate.get(aspectTypeQName);
        policy.onAddAspect(nodeRef, aspectTypeQName);
    }
    
    /**
     * @see NodeServicePolicies.BeforeRemoveAspectPolicy#BeforeRemoveAspect(NodeRef, QName)
     */
    protected void invokeBeforeRemoveAspect(NodeRef nodeRef, QName aspectTypeQName)
    {
        NodeServicePolicies.BeforeRemoveAspectPolicy policy = beforeRemoveAspectDelegate.get(aspectTypeQName);
        policy.beforeRemoveAspect(nodeRef, aspectTypeQName);
    }
    
    /**
     * @see NodeServicePolicies.OnRemoveAspectPolicy#onRemoveAspect(NodeRef, QName)
     */
    protected void invokeOnRemoveAspect(NodeRef nodeRef, QName aspectTypeQName)
    {
        NodeServicePolicies.OnRemoveAspectPolicy policy = onRemoveAspectDelegate.get(aspectTypeQName);
        policy.onRemoveAspect(nodeRef, aspectTypeQName);
    }
    
    /**
     * @see NodeServicePolicies.BeforeCreateChildAssociationPolicy#beforeCreateChildAssociation(NodeRef, NodeRef, QName, QName)
     */
    protected void invokeBeforeCreateChildAssociation(
            NodeRef parentNodeRef,
            NodeRef childNodeRef,
            QName assocTypeQName,
            QName assocQName)
    {
        // get qnames to invoke against
        Set<QName> qnames = getTypeAndAspectQNames(parentNodeRef);
        // execute policy for node type
        NodeServicePolicies.BeforeCreateChildAssociationPolicy policy =
            beforeCreateChildAssociationDelegate.get(qnames, assocTypeQName);
        policy.beforeCreateChildAssociation(parentNodeRef, childNodeRef, assocTypeQName, assocQName);
    }

    /**
     * @see NodeServicePolicies.OnCreateChildAssociationPolicy#onCreateChildAssociation(ChildAssociationRef)
     */
    protected void invokeOnCreateChildAssociation(ChildAssociationRef childAssocRef)
    {
		// Get the parent reference and the assoc type qName
        NodeRef parentNodeRef = childAssocRef.getParentRef();
        QName assocTypeQName = childAssocRef.getTypeQName();
        // get qnames to invoke against
        Set<QName> qnames = getTypeAndAspectQNames(parentNodeRef);
        // execute policy for node type and aspects
		NodeServicePolicies.OnCreateChildAssociationPolicy policy =
            onCreateChildAssociationDelegate.get(qnames, assocTypeQName);
        policy.onCreateChildAssociation(childAssocRef);
    }

    /**
     * @see NodeServicePolicies.BeforeDeleteChildAssociationPolicy#beforeDeleteChildAssociation(ChildAssociationRef)
     */
    protected void invokeBeforeDeleteChildAssociation(ChildAssociationRef childAssocRef)
    {
        NodeRef parentNodeRef = childAssocRef.getParentRef();
        QName assocTypeQName = childAssocRef.getTypeQName();
        // get qnames to invoke against
        Set<QName> qnames = getTypeAndAspectQNames(parentNodeRef);
        // execute policy for node type and aspects
        NodeServicePolicies.BeforeDeleteChildAssociationPolicy policy =
            beforeDeleteChildAssociationDelegate.get(qnames, assocTypeQName);
        policy.beforeDeleteChildAssociation(childAssocRef);
    }
    
    /**
     * @see NodeServicePolicies.OnDeleteChildAssociationPolicy#onDeleteChildAssociation(ChildAssociationRef)
     */
    protected void invokeOnDeleteChildAssociation(ChildAssociationRef childAssocRef)
    {
        NodeRef parentNodeRef = childAssocRef.getParentRef();
        QName assocTypeQName = childAssocRef.getTypeQName();
        // get qnames to invoke against
        Set<QName> qnames = getTypeAndAspectQNames(parentNodeRef);
        // execute policy for node type and aspects
        NodeServicePolicies.OnDeleteChildAssociationPolicy policy =
            onDeleteChildAssociationDelegate.get(qnames, assocTypeQName);
        policy.onDeleteChildAssociation(childAssocRef);
    }
    
    /**
     * @see NodeServicePolicies.BeforeCreateAssociationPolicy#beforeCreateAssociation(NodeRef, NodeRef, QName)
     */
    protected void invokeBeforeCreateAssociation(
            NodeRef sourceNodeRef,
            NodeRef targetNodeRef,
            QName assocTypeQName)
    {
        // get qnames to invoke against
        Set<QName> qnames = getTypeAndAspectQNames(sourceNodeRef);
        // execute policy for node type and aspects
        NodeServicePolicies.BeforeCreateAssociationPolicy policy =
            beforeCreateAssociationDelegate.get(qnames, assocTypeQName);
        policy.beforeCreateAssociation(sourceNodeRef, targetNodeRef, assocTypeQName);
    }

    /**
     * @see NodeServicePolicies.BeforeDeleteAssociationPolicy#beforeDeleteAssociation(AssociationRef)
     */
    protected void invokeBeforeDeleteAssociation(AssociationRef nodeAssocRef)
    {
        NodeRef sourceNodeRef = nodeAssocRef.getSourceRef();
        QName assocTypeQName = nodeAssocRef.getTypeQName();
        // get qnames to invoke against
        Set<QName> qnames = getTypeAndAspectQNames(sourceNodeRef);
        // execute policy for node type and aspects
        NodeServicePolicies.BeforeDeleteAssociationPolicy policy =
            beforeDeleteAssociationDelegate.get(qnames, assocTypeQName);
        policy.beforeDeleteAssociation(nodeAssocRef);
    }
    
    /**
     * Get all aspect and node type qualified names
     * 
     * @param nodeRef the node we are interested in
     * @return Returns a set of qualified names containing the node type and all
     *      the node aspects
     */
    protected Set<QName> getTypeAndAspectQNames(NodeRef nodeRef)
    {
        Set<QName> aspectQNames = getAspects(nodeRef);
        QName typeQName = getType(nodeRef);
        // combine
        Set<QName> qnames = new HashSet<QName>(aspectQNames);
        qnames.add(typeQName);
        // done
        return qnames;
    }
    
    /**
     * Defers to the pattern matching overload
     * 
     * @see RegexQNamePattern#MATCH_ALL
     * @see NodeService#getParentAssocs(NodeRef, QNamePattern)
     */
    public List<ChildAssociationRef> getParentAssocs(NodeRef nodeRef) throws InvalidNodeRefException
    {
        return getParentAssocs(nodeRef, RegexQNamePattern.MATCH_ALL);
    }

    /**
     * Defers to the pattern matching overload
     * 
     * @see RegexQNamePattern#MATCH_ALL
     * @see NodeService#getChildAssocs(NodeRef, QNamePattern)
     */
    public final List<ChildAssociationRef> getChildAssocs(NodeRef nodeRef) throws InvalidNodeRefException
    {
        return getChildAssocs(nodeRef, RegexQNamePattern.MATCH_ALL);
    }

    public List<ChildAssociationRef> selectNodes(
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
            List<ChildAssociationRef> answer = new ArrayList<ChildAssociationRef>(list.size());
            for(Object o: list)
            {
                if(!(o instanceof ChildAssociationRef))
                {
                    throw new XPathException("Xpath expression must only select nodes");
                }
                answer.add((ChildAssociationRef)o);
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
     * @see #setSearcher(SearchService)
     */
    public boolean contains(NodeRef nodeRef, QName propertyQName, String googleLikePattern)
    {
        ResultSet resultSet = null;
        try
        {
            if (searcher == null || indexer == null)
            {
                // without both, no Lucene search is possible
                return false;
            }
            // build Lucene search string specific to the node
            StringBuilder sb = new StringBuilder();
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

            resultSet = searcher.query(nodeRef.getStoreRef(), "lucene", sb.toString());
            boolean answer = resultSet.length() > 0;
            return answer;
        }
        finally
        {
            if (resultSet != null)
            {
                resultSet.close();
            }
        }
    }

    /**
     * @return Returns true if the pattern is present, otherwise false.
     * 
     * @see #setIndexer(Indexer)
     * @see #setSearcher(SearchService)
     */
    public boolean like(NodeRef nodeRef, QName propertyQName, String sqlLikePattern)
    {
        ResultSet resultSet = null;
        try
        {
            if (searcher == null || indexer == null)
            {
                // without both, no Lucene search is possible
                return false;
            }

            // Need to turn the SQL like patter into lucene line
            // Need to replace unescaped % with * (? and ? will match and \ is
            // used
            // for escape so the rest is OK)

            // replace the SQL-like search string with appropriate Lucene syntax
            StringBuilder sb = new StringBuilder();
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
            sb = new StringBuilder();
            sb.append("+ID:").append(nodeRef.getId()).append(" +(TEXT:(").append(pattern).append(") ");
            if (propertyQName != null)
            {
                sb.append("@").append(LuceneQueryParser.escape(propertyQName.toString())).append(":(").append(pattern).append(")");
            }
            sb.append(")");

            resultSet = searcher.query(nodeRef.getStoreRef(), "lucene", sb.toString());
            boolean answer = resultSet.length() > 0;
            return answer;
        }
        finally
        {
            if (resultSet != null)
            {
                resultSet.close();
            }
        }
    }

    protected Indexer getIndexer()
    {
        return indexer;
    }

    protected SearchService getSearcher()
    {
        return searcher;
    }
}
