/**
 * Created on Apr 5, 2005
 */
package org.alfresco.repo.version.lightweight;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.repo.dictionary.impl.DictionaryBootstrap;
import org.alfresco.repo.ref.qname.RegexQNamePattern;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.InvalidAspectException;
import org.alfresco.service.cmr.repository.AssociationExistsException;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.EntityRef;
import org.alfresco.service.cmr.repository.InvalidNodeRefException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.Path;
import org.alfresco.service.cmr.repository.PropertyException;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.QueryParameterDefinition;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.QNamePattern;
import org.apache.lucene.search.Searcher;

/**
 * The light weight version store node service implementation.
 * 
 * @author Roy Wetherall
 */
public class NodeServiceImpl implements NodeService, VersionStoreConst 
{
    /**
     * Error messages
     */
    private final static String MSG_UNSUPPORTED = 
        "This operation is not supported by a version store implementation of the node service.";
	
    /**
     * The db node service, used as the version store implementation
     */
    protected NodeService dbNodeService;

    /**
     * The repository searcher
     */
    private Searcher searcher;
    
    /**
     * The dictionary service
     */
    protected DictionaryService dicitionaryService;
	
    
    /**
     * Sets the db node service, used as the version store implementation
     * 
     * @param nodeService  the node service
     */
    public void setDbNodeService(NodeService nodeService)
    {
        this.dbNodeService = nodeService;
    }

    /**
     * Sets the searcher
     * 
     * @param searcher  the searcher
     */
    public void setSearcher(Searcher searcher)
    {
        this.searcher = searcher; 
    }
    
    /**
     * Sets the dictionary service
     * 
     * @param dictionaryService  the dictionary service
     */
    public void setDictionaryService(DictionaryService dictionaryService)
    {
        this.dicitionaryService = dictionaryService;
    }
	
    /**
     * Delegates to the <code>NodeService</code> used as the version store implementation
     */
    public StoreRef createStore(String protocol, String identifier)
    {
        return dbNodeService.createStore(protocol, identifier);
    }

    /**
     * Delegates to the <code>NodeService</code> used as the version store implementation
     */
    public boolean exists(StoreRef storeRef)
    {
        return dbNodeService.exists(storeRef);
    }

    /**
     * Delegates to the <code>NodeService</code> used as the version store implementation
     */
    public boolean exists(NodeRef nodeRef)
    {
        return dbNodeService.exists(convertNodeRef(nodeRef));
    }
    
    /**
     * Convert the incomming node ref (with the version store protocol specified)
     * to the internal representation with the workspace protocol.
     * 
     * @param nodeRef   the incomming verison protocol node reference
     * @return          the internal version node reference
     */
    private NodeRef convertNodeRef(NodeRef nodeRef)
    {
        return new NodeRef(new StoreRef(StoreRef.PROTOCOL_WORKSPACE, STORE_ID), nodeRef.getId());
    }

    /**
     * Delegates to the <code>NodeService</code> used as the version store implementation
     */
    public NodeRef getRootNode(StoreRef storeRef)
    {
        return dbNodeService.getRootNode(storeRef);
    }

    /**
     * @throws UnsupportedOperationException always
     */
    public ChildAssociationRef createNode(
			NodeRef parentRef,
            QName assocTypeQName,
            QName assocQName,
            QName nodeTypeQName) throws InvalidNodeRefException
    {
        // This operation is not supported for a verion store
        throw new UnsupportedOperationException(MSG_UNSUPPORTED);
    }
    
    /**
     * @throws UnsupportedOperationException always
     */
    public ChildAssociationRef createNode(
			NodeRef parentRef,
            QName assocTypeQName,
            QName assocQName,
            QName nodeTypeQName,
            Map<QName, Serializable> properties) throws InvalidNodeRefException
    {
        // This operation is not supported for a verion store
        throw new UnsupportedOperationException(MSG_UNSUPPORTED);
    }
    
    /**
     * @throws UnsupportedOperationException always
     */
    public void deleteNode(NodeRef nodeRef) throws InvalidNodeRefException
    {
        // This operation is not supported for a verion store
        throw new UnsupportedOperationException(MSG_UNSUPPORTED);
    }
    
    /**
     * @throws UnsupportedOperationException always
     */
    public ChildAssociationRef addChild(NodeRef parentRef,
            NodeRef childRef,
            QName assocTypeQName,
            QName qname) throws InvalidNodeRefException
    {
        // This operation is not supported for a verion store
        throw new UnsupportedOperationException(MSG_UNSUPPORTED);
    }
    
    /**
     * @throws UnsupportedOperationException always
     */
    public Collection<EntityRef> removeChild(NodeRef parentRef, NodeRef childRef) throws InvalidNodeRefException
    {
        // This operation is not supported for a verion store
        throw new UnsupportedOperationException(MSG_UNSUPPORTED);
    }

    /**
     * @throws UnsupportedOperationException always
     */
    public ChildAssociationRef moveNode(NodeRef nodeToMoveRef, NodeRef newParentRef, QName assocTypeQName, QName assocQName) throws InvalidNodeRefException
    {
        throw new UnsupportedOperationException(MSG_UNSUPPORTED);
    }

    /**
     * Type translation for version store
     */
    public QName getType(NodeRef nodeRef) throws InvalidNodeRefException
    {
		return (QName)this.dbNodeService.getProperty(convertNodeRef(nodeRef), PROP_QNAME_FROZEN_NODE_TYPE);
    }
    
    /**
     * @throws UnsupportedOperationException always
     */
    public void addAspect(NodeRef nodeRef, QName aspectRef, Map<QName, Serializable> aspectProperties) throws InvalidNodeRefException, InvalidAspectException, PropertyException
    {
        // This operation is not supported for a verion store
        throw new UnsupportedOperationException(MSG_UNSUPPORTED);
    }

    /**
     * Translation for version store
     */
    public boolean hasAspect(NodeRef nodeRef, QName aspectRef) throws InvalidNodeRefException, InvalidAspectException
    {
        Set<QName> aspects = (Set<QName>)this.dbNodeService.getProperty(convertNodeRef(nodeRef), PROP_QNAME_FROZEN_ASPECTS);
        return aspects.contains(aspectRef);
    }

    /**
     * @throws UnsupportedOperationException always
     */
    public void removeAspect(NodeRef nodeRef, QName aspectRef) throws InvalidNodeRefException, InvalidAspectException
    {
        // This operation is not supported for a verion store
        throw new UnsupportedOperationException(MSG_UNSUPPORTED);
    }

    /**
     * Translation for version store
     */
    public Set<QName> getAspects(NodeRef nodeRef) throws InvalidNodeRefException
    {
        return (Set<QName>)this.dbNodeService.getProperty(convertNodeRef(nodeRef), PROP_QNAME_FROZEN_ASPECTS);
    }

    /**
     * Property translation for version store
     */
    public Map<QName, Serializable> getProperties(NodeRef nodeRef) throws InvalidNodeRefException
    {
		Map<QName, Serializable> result = new HashMap<QName, Serializable>();
		
        // TODO should be doing this using a path query ..
        
        Collection<ChildAssociationRef> children = this.dbNodeService.getChildAssocs(convertNodeRef(nodeRef));
        for (ChildAssociationRef child : children)
        {
            if (child.getQName().equals(CHILD_QNAME_VERSIONED_ATTRIBUTES))
            {
                NodeRef versionedAttribute = child.getChildRef();
                
                // Get the QName and the value
                QName qName = (QName)this.dbNodeService.getProperty(versionedAttribute, PROP_QNAME_QNAME);
                Serializable value = this.dbNodeService.getProperty(versionedAttribute, PROP_QNAME_VALUE);
                
                result.put(qName, value);
            }
        }        
        
		return result;
    }
    
    /**
     * Property translation for version store
     */
    public Serializable getProperty(NodeRef nodeRef, QName qname) throws InvalidNodeRefException
    {        
        // TODO should be doing this with a search ...
        
        Map<QName, Serializable> properties = getProperties(convertNodeRef(nodeRef));
        return properties.get(qname);			
    }
    
    /**
     * @throws UnsupportedOperationException always
     */
    public void setProperties(NodeRef nodeRef, Map<QName, Serializable> properties) throws InvalidNodeRefException
    {
        // This operation is not supported for a verion store
        throw new UnsupportedOperationException(MSG_UNSUPPORTED);
    }
    
    /**
     * @throws UnsupportedOperationException always
     */
    public void setProperty(NodeRef nodeRef, QName qame, Serializable value) throws InvalidNodeRefException
    {
        // This operation is not supported for a verion store
        throw new UnsupportedOperationException(MSG_UNSUPPORTED);
    }
    
    /**
     * @throws UnsupportedOperationException always
     */
    public List<ChildAssociationRef> getParentAssocs(NodeRef nodeRef)
    {
        // This operation is not supported for a verion store
        throw new UnsupportedOperationException(MSG_UNSUPPORTED);
    }

    /**
     * @see NodeService#getChildAssocs(NodeRef)
     */
    public List<ChildAssociationRef> getParentAssocs(NodeRef nodeRef, QNamePattern qnamePattern)
    {
        // This operation is not supported for a verion store
        throw new UnsupportedOperationException(MSG_UNSUPPORTED);
    }

    /**
     * @see RegexQNamePattern#MATCH_ALL
     * @see #getChildAssocs(NodeRef, QNamePattern)
     */
    public List<ChildAssociationRef> getChildAssocs(NodeRef nodeRef) throws InvalidNodeRefException
    {
        return getChildAssocs(convertNodeRef(nodeRef), RegexQNamePattern.MATCH_ALL);
    }

    /**
     * Performs conversion from version store properties to <i>real</i> associations
     */
    public List<ChildAssociationRef> getChildAssocs(NodeRef nodeRef, QNamePattern qnamePattern) throws InvalidNodeRefException
    {
        List<ChildAssociationRef> result = new ArrayList<ChildAssociationRef>();
        
        // Get the child assocs from the version store
        List<ChildAssociationRef> childAssocRefs = this.dbNodeService.getChildAssocs(
                convertNodeRef(nodeRef),
                CHILD_QNAME_VERSIONED_CHILD_ASSOCS);
        for (ChildAssociationRef childAssocRef : childAssocRefs)
        {
            // Get the child reference
            NodeRef childRef = childAssocRef.getChildRef();
            NodeRef referencedNode = (NodeRef)this.dbNodeService.getProperty(childRef, DictionaryBootstrap.PROP_QNAME_REFERENCE); 
            
            // get the qualified name of the frozen child association and filter out unwanted names
            QName qName = (QName)this.dbNodeService.getProperty(childRef, PROP_QNAME_ASSOC_QNAME);
            
            if (qnamePattern.isMatch(qName) == true)
            {
                // Check to see if the versioned node is a version history
                QName classRef = this.dbNodeService.getType(referencedNode);
                if (TYPE_QNAME_VERSION_HISTORY.equals(classRef) == true)
                {
                    // Return a reference to the node in the correct workspace
                    String childRefId = (String)this.dbNodeService.getProperty(referencedNode, PROP_QNAME_VERSIONED_NODE_ID);
                    childRef = new NodeRef(nodeRef.getStoreRef(), childRefId);                                                
                }
                
                // Retrieve the isPrimary and nthSibling values of the forzen child association
                QName assocType = (QName)this.dbNodeService.getProperty(childRef, PROP_QNAME_ASSOC_TYPE_QNAME);
                boolean isPrimary = ((Boolean)this.dbNodeService.getProperty(childRef, PROP_QNAME_IS_PRIMARY)).booleanValue();
                int nthSibling = ((Integer)this.dbNodeService.getProperty(childRef, PROP_QNAME_NTH_SIBLING)).intValue();
                
                // Build a child assoc ref to add to the returned list
                ChildAssociationRef newChildAssocRef = new ChildAssociationRef(
                        assocType,
                        nodeRef, 
                        qName, 
                        referencedNode, 
                        isPrimary, 
                        nthSibling);
                result.add(newChildAssocRef);
            }
        }
        
        return result;
    }
    
    /**
     * @throws UnsupportedOperationException always
     */
    public ChildAssociationRef getPrimaryParent(NodeRef nodeRef) throws InvalidNodeRefException
    {
        // This operation is not supported for a verion store
        throw new UnsupportedOperationException(MSG_UNSUPPORTED);
    }
    
    /**
     * @throws UnsupportedOperationException always
     */
    public AssociationRef createAssociation(NodeRef sourceRef, NodeRef targetRef, QName assocTypeQName)
            throws InvalidNodeRefException, AssociationExistsException
    {
        // This operation is not supported for a verion store
        throw new UnsupportedOperationException(MSG_UNSUPPORTED);
    }
    
    /**
     * @throws UnsupportedOperationException always
     */
    public void removeAssociation(NodeRef sourceRef, NodeRef targetRef, QName assocTypeQName)
    {
        // This operation is not supported for a verion store
        throw new UnsupportedOperationException(MSG_UNSUPPORTED);
    }
    
    /**
     * @throws UnsupportedOperationException always
     */
    public List<AssociationRef> getTargetAssocs(NodeRef sourceRef, QNamePattern qnamePattern)
    {
        List<AssociationRef> result = new ArrayList<AssociationRef>();
        
        // Get the child assocs from the version store
        List<ChildAssociationRef> childAssocRefs = this.dbNodeService.getChildAssocs(
                convertNodeRef(sourceRef),
                CHILD_QNAME_VERSIONED_ASSOCS);
        for (ChildAssociationRef childAssocRef : childAssocRefs)
        {
            // Get the assoc reference
            NodeRef childRef = childAssocRef.getChildRef();
            NodeRef referencedNode = (NodeRef)this.dbNodeService.getProperty(childRef, DictionaryBootstrap.PROP_QNAME_REFERENCE); 
            
            // get the qualified type name of the frozen child association and filter out unwanted names
            QName qName = (QName)this.dbNodeService.getProperty(childRef, PROP_QNAME_ASSOC_TYPE_QNAME);
            
            if (qnamePattern.isMatch(qName) == true)
            {
                // Check to see if the versioned node is a version history
                QName classRef = this.dbNodeService.getType(referencedNode);
                if (TYPE_QNAME_VERSION_HISTORY.equals(classRef) == true)
                {
                    // Return a reference to the node in the correct workspace
                    String childRefId = (String)this.dbNodeService.getProperty(referencedNode, PROP_QNAME_VERSIONED_NODE_ID);
                    childRef = new NodeRef(sourceRef.getStoreRef(), childRefId);                    
                }
                
                AssociationRef newAssocRef = new AssociationRef(sourceRef, qName, childRef);
                result.add(newAssocRef);
            }
        }
        
        return result;
    }
    
    /**
     * @throws UnsupportedOperationException always
     */
    public List<AssociationRef> getSourceAssocs(NodeRef sourceRef, QNamePattern qnamePattern)
    {
        // This operation is not supported for a verion store
        throw new UnsupportedOperationException(MSG_UNSUPPORTED);
    }
    
    /**
     * @throws UnsupportedOperationException always
     */
    public Path getPath(NodeRef nodeRef) throws InvalidNodeRefException
    {
        // This operation is not supported for a verion store
        throw new UnsupportedOperationException(MSG_UNSUPPORTED);
    }
    
    /**
     * @throws UnsupportedOperationException always
     */
    public Collection<Path> getPaths(NodeRef nodeRef, boolean primaryOnly) throws InvalidNodeRefException
    {
        // This operation is not supported for a verion store
        throw new UnsupportedOperationException(MSG_UNSUPPORTED);
    }

    public List<ChildAssociationRef> selectNodes(NodeRef contextNode, String XPath, QueryParameterDefinition[] parameters, NamespacePrefixResolver namespacePrefixResolver, boolean followAllParentLinks)
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException(MSG_UNSUPPORTED);
    }

    public List<Serializable> selectProperties(NodeRef contextNode, String XPath, QueryParameterDefinition[] parameters, NamespacePrefixResolver namespacePrefixResolver, boolean followAllParentLinks)
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException(MSG_UNSUPPORTED);
    }

    public boolean contains(NodeRef nodeRef, QName property, String sqlLikePattern)
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    public boolean like(NodeRef nodeRef, QName property, String sqlLikePattern)
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }
    
    
}
