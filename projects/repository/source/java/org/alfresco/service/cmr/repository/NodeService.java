package org.alfresco.service.cmr.repository;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.service.cmr.dictionary.InvalidAspectException;
import org.alfresco.service.cmr.dictionary.InvalidTypeException;
import org.alfresco.service.cmr.search.QueryParameterDefinition;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.QNamePattern;

/**
 * Interface for public and internal <b>node</b> and <b>store</b> operations.
 * 
 * @author Derek Hulley
 */
public interface NodeService
{
    /**
     * Create a new store for the given protocol and identifier.  The implementation
     * may create the store in any number of locations, including a database or
     * Subversion.
     * 
     * @param protocol the implementation protocol
     * @param identifier the protocol-specific identifier
     * @return Returns a reference to the store
     * @throws StoreExistsException
     */
    public StoreRef createStore(String protocol, String identifier) throws StoreExistsException;
    
    /**
     * @param storeRef a reference to the store to look for
     * @return Returns true if the store exists, otherwise false
     */
    public boolean exists(StoreRef storeRef);
    
    /**
     * @param nodeRef a reference to the node to look for
     * @return Returns true if the node exists, otherwise false
     */
    public boolean exists(NodeRef nodeRef);
    
    /**
     * @param storeRef a reference to an existing store
     * @return Returns a reference to the root node of the store
     * @throws InvalidStoreRefException if the store could not be found
     */
    public NodeRef getRootNode(StoreRef storeRef) throws InvalidStoreRefException;

    /**
     * @see #createNode(NodeRef, QName, QName, QName, Map<QName,Serializable>)
     */
    public ChildAssociationRef createNode(
            NodeRef parentRef,
            QName assocTypeQName,
            QName assocQName,
            QName nodeTypeQName)
            throws InvalidNodeRefException, InvalidTypeException, PropertyException;
    
    /**
     * Creates a new, non-abstract, real node as a primary child of the given parent node.
     * 
     * @param parentRef the parent node
     * @param assocTypeQName the type of the association to create.  This is used
     *      for verification against the data dictionary.
     * @param assocQName the qualified name of the association
     * @param nodeTypeQName a reference to the node type
     * @param properties optional map of properties to keyed by their qualified names
     * @return Returns a reference to the newly created child association
     * @throws InvalidNodeRefException if the parent reference is invalid
     * @throws InvalidTypeException if the node type reference is not recognised
     * @throws PropertyException if a mandatory property was not set
     * 
     * @see org.alfresco.service.cmr.dictionary.DictionaryService
     */
    public ChildAssociationRef createNode(
            NodeRef parentRef,
            QName assocTypeQName,
            QName assocQName,
            QName nodeTypeQName,
            Map<QName, Serializable> properties)
            throws InvalidNodeRefException, InvalidTypeException, PropertyException;
    
    /**
     * Moves the primary location of the given node.
     * <p>
     * This involves changing the node's primary parent and possibly the name of the
     * association referencing it.
     *  
     * @param nodeToMoveRef the node to move
     * @param newParentRef the new parent of the moved node
     * @param assocTypeQName the type of the association to create.  This is used
     *      for verification against the data dictionary.
     * @param assocQName the qualified name of the new child association
     * @return Returns a reference to the newly created child association
     * @throws InvalidNodeRefException if either the parent node or move node reference is invalid
     * 
     * @see #getPrimaryParent(NodeRef)
     */
    public ChildAssociationRef moveNode(
            NodeRef nodeToMoveRef,
            NodeRef newParentRef,
            QName assocTypeQName,
            QName assocQName)
            throws InvalidNodeRefException;
    
    /**
     * @param nodeRef
     * @return Returns the type name
     * @throws InvalidNodeRefException if the node could not be found
     * 
     * @see org.alfresco.service.cmr.dictionary.DictionaryService
     */
    public QName getType(NodeRef nodeRef) throws InvalidNodeRefException;
    
    /**
     * Applies an aspect to the given node.  After this method has been called,
     * the node with have all the aspect-related properties present
     * 
     * @param nodeRef
     * @param aspectRef the aspect to apply to the node
     * @param aspectProperties a minimum of the mandatory properties required for
     *      the aspect
     * @throws InvalidNodeRefException
     * @throws InvalidAspectException if the class reference is not to a valid aspect
     * @throws PropertyException if one of the aspect's required
     *      properties have not been provided
     *
     * @see org.alfresco.service.cmr.dictionary.DictionaryService#getAspect(QName)
     * @see org.alfresco.service.cmr.dictionary.ClassDefinition#getProperties()
     */
    public void addAspect(
            NodeRef nodeRef,
            QName aspectRef,
            Map<QName, Serializable> aspectProperties)
            throws InvalidNodeRefException, InvalidAspectException, PropertyException;
    
    /**
     * Remove an aspect and all related properties from a node
     * 
     * @param nodeRef
     * @param aspectRef the type of aspect to remove
     * @throws InvalidNodeRefException if the node could not be found
     * @throws InvalidAspectException if the the aspect is unknown or if the
     *      aspect is mandatory for the <b>class</b> of the <b>node</b>
     */
    public void removeAspect(NodeRef nodeRef, QName aspectRef)
            throws InvalidNodeRefException, InvalidAspectException;
    
    /**
     * Determines if a given aspect is present on a node.  Aspects may only be
     * removed if they are <b>NOT</b> mandatory.
     * 
     * @param nodeRef
     * @param aspectRef
     * @return Returns true if the aspect has been applied to the given node,
     *      otherwise false
     * @throws InvalidNodeRefException if the node could not be found
     * @throws InvalidAspectException if the aspect reference is invalid
     */
    public boolean hasAspect(NodeRef nodeRef, QName aspectRef)
            throws InvalidNodeRefException, InvalidAspectException;
    
    /**
     * @param nodeRef
     * @return Returns a set of all aspects applied to the node, including mandatory
     *      aspects
     * @throws InvalidNodeRefException if the node could not be found
     */
    public Set<QName> getAspects(NodeRef nodeRef) throws InvalidNodeRefException;
    
    /**
     * Deletes the given node.
     * 
     * @param nodeRef reference to a node within a store
     * @throws InvalidNodeRefException if the reference given is invalid
     */
    public void deleteNode(NodeRef nodeRef) throws InvalidNodeRefException;
    
    /**
     * Makes a parent-child association between the given nodes.  Both nodes must belong to the same store.
     * 
     * @param parentRef
     * @param childRef 
     * @param assocTypeQName the qualified name of the association type as defined in the datadictionary
     * @param qname the qualified name of the association
     * @return Returns a reference to the newly created child association
     * @throws InvalidNodeRefException if the parent or child nodes could not be found
     */
    public ChildAssociationRef addChild(
            NodeRef parentRef,
            NodeRef childRef,
            QName assocTypeQName,
            QName qname) throws InvalidNodeRefException;
    
    /**
     * Severs all parent-child relationships between two nodes.
     * <p>
     * The child node will be cascade deleted if one of the associations was the
     * primary association, i.e. the one with which the child node was created.
     * 
     * @param parentRef the parent end of the association
     * @param childRef the child end of the association
     * @return Returns a collection of deleted entities - both associations and node references.
     * @throws InvalidNodeRefException if the parent or child nodes could not be found
     */
    public Collection<EntityRef> removeChild(NodeRef parentRef, NodeRef childRef) throws InvalidNodeRefException;

    /**
     * @param nodeRef
     * @return Returns all properties keyed by their qualified name
     * @throws InvalidNodeRefException if the node could not be found
     */
    public Map<QName, Serializable> getProperties(NodeRef nodeRef) throws InvalidNodeRefException;
    
    /**
     * @param nodeRef
     * @param qname the qualified name of the property
     * @return Returns the value of the property, or null if not yet set
     * @throws InvalidNodeRefException if the node could not be found
     */
    public Serializable getProperty(NodeRef nodeRef, QName qname) throws InvalidNodeRefException;
    
    /**
     * Set the values of all properties to be an <code>Serializable</code> instances.
     * The properties given must still fulfill the requirements of the class and
     * aspects relevant to the node.
     * <p>
     * <b>NOTE:</b> Null values <u>are</u> allowed.
     * 
     * @param nodeRef
     * @param properties all the properties of the node keyed by their qualified names
     * @throws InvalidNodeRefException if the node could not be found
     */
    public void setProperties(NodeRef nodeRef, Map<QName, Serializable> properties) throws InvalidNodeRefException;
    
    /**
     * Sets the value of a property to be any <code>Serializable</code> instance.
     * To remove a property value, use {@link #getProperties(NodeRef)}, remove the
     * value and call {@link #setProperties(NodeRef, Map<QName,Serializable>)}.
     * <p>
     * <b>NOTE:</b> Null values <u>are</u> allowed.
     * 
     * @param nodeRef
     * @param qname the fully qualified name of the property
     * @param propertyValue the value of the property - never null
     * @throws InvalidNodeRefException if the node could not be found
     */
    public void setProperty(NodeRef nodeRef, QName qname, Serializable value) throws InvalidNodeRefException;
    
    /**
     * @param nodeRef the child node
     * @return Returns a list of all parent-child associations that exist where the given
     *      node is the child
     * @throws InvalidNodeRefException if the node could not be found
     * 
     * @see #getParentAssocs(NodeRef, QNamePattern)
     */
    public List<ChildAssociationRef> getParentAssocs(NodeRef nodeRef) throws InvalidNodeRefException;
    
    /**
     * Gets all parent associations where the pattern of the association qualified
     * name is a match
     * 
     * @param nodeRef the child node
     * @param qnamePattern the pattern that the qnames of the assocs must match
     * @return Returns a list of all parent-child associations that exist where the given
     *      node is the child
     * @throws InvalidNodeRefException if the node could not be found
     * 
     * @see QName
     * @see org.alfresco.service.namespace.RegexQNamePattern#MATCH_ALL
     */
    public List<ChildAssociationRef> getParentAssocs(NodeRef nodeRef, QNamePattern qnamePattern)
            throws InvalidNodeRefException;
    
    /**
     * @param nodeRef the parent node - usually a <b>container</b>
     * @return Returns a collection of <code>ChildAssocRef</code> instances.  If the
     *      node is not a <b>container</b> then the result will be empty.
     * @throws InvalidNodeRefException if the node could not be found
     * 
     * @see #getChildAssocs(NodeRef, QNamePattern)
     */
    public List<ChildAssociationRef> getChildAssocs(NodeRef nodeRef) throws InvalidNodeRefException;
    
    /**
     * Gets all child associations where the pattern of the association qualified
     * name is a match.
     * 
     * @param nodeRef the parent node - usually a <b>container</b>
     * @param qnamePattern the pattern that the qnames of the assocs must match
     * @return Returns a list of <code>ChildAssocRef</code> instances.  If the
     *      node is not a <b>container</b> then the result will be empty.
     * @throws InvalidNodeRefException if the node could not be found
     * 
     * @see QName
     * @see org.alfresco.service.namespace.RegexQNamePattern#MATCH_ALL
     */
    public List<ChildAssociationRef> getChildAssocs(NodeRef nodeRef, QNamePattern qnamePattern)
            throws InvalidNodeRefException;
    
    /**
     * Fetches the primary parent-child relationship.
     * <p>
     * For a root node, the parent node reference will be null.
     * 
     * @param nodeRef
     * @return Returns the primary parent-child association of the node
     * @throws InvalidNodeRefException if the node could not be found
     */
    public ChildAssociationRef getPrimaryParent(NodeRef nodeRef) throws InvalidNodeRefException;
    
    /**
     * 
     * @param sourceRef a reference to a <b>real</b> node
     * @param targetRef a reference to a node
     * @param assocTypeQName the qualified name of the association type
     * @return Returns a reference to the new association
     * @throws InvalidNodeRefException if either of the nodes could not be found
     * @throws AssociationExistsException
     */
    public AssociationRef createAssociation(NodeRef sourceRef, NodeRef targetRef, QName assocTypeQName)
            throws InvalidNodeRefException, AssociationExistsException;
    
    /**
     * 
     * @param sourceRef the associaton source node
     * @param targetRef the association target node
     * @param assocTypeQName the qualified name of the association type
     * @throws InvalidNodeRefException if either of the nodes could not be found
     */
    public void removeAssociation(NodeRef sourceRef, NodeRef targetRef, QName assocTypeQName)
            throws InvalidNodeRefException;
    
    /**
     * Fetches all associations <i>from</i> the given source where the associations'
     * qualified names match the pattern provided.
     * 
     * @param sourceRef the association source
     * @param qnamePattern the association qname pattern to match against
     * @return Returns a list of <code>NodeAssocRef</code> instances for which the
     *      given node is a source
     * @throws InvalidNodeRefException if the source node could not be found
     * 
     * @see QName
     * @see org.alfresco.service.namespace.RegexQNamePattern#MATCH_ALL
     */
    public List<AssociationRef> getTargetAssocs(NodeRef sourceRef, QNamePattern qnamePattern)
            throws InvalidNodeRefException;
    
    /**
     * Fetches all associations <i>to</i> the given target where the associations'
     * qualified names match the pattern provided.
     * 
     * @param targetRef the association target
     * @param qnamePattern the association qname pattern to match against
     * @return Returns a list of <code>NodeAssocRef</code> instances for which the
     *      given node is a target
     * @throws InvalidNodeRefException
     * 
     * @see QName
     * @see org.alfresco.service.namespace.RegexQNamePattern#MATCH_ALL
     */
    public List<AssociationRef> getSourceAssocs(NodeRef targetRef, QNamePattern qnamePattern)
            throws InvalidNodeRefException;
    
    /**
     * The root node has an entry in the path(s) returned.  For this reason, there
     * will always be <b>at least one</b> path element in the returned path(s).
     * The first element will have a null parent reference and qname.
     * 
     * @param nodeRef
     * @return Returns the path to the node along the primary node path
     * @throws InvalidNodeRefException if the node could not be found
     * 
     * @see #getPaths(NodeRef, boolean)
     */
    public Path getPath(NodeRef nodeRef) throws InvalidNodeRefException;
    
    /**
     * The root node has an entry in the path(s) returned.  For this reason, there
     * will always be <b>at least one</b> path element in the returned path(s).
     * The first element will have a null parent reference and qname.
     * 
     * @param nodeRef
     * @param primaryOnly true if only the primary path must be retrieved.  If true, the
     *      result will have exactly one entry.
     * @return Returns a collection of all possible paths to the given node
     * @throws InvalidNodeRefException if the node could not be found
     */
    public Collection<Path> getPaths(NodeRef nodeRef, boolean primaryOnly) throws InvalidNodeRefException;
    
    /**
     * Select nodes using an xpath expression.
     * 
     * @param contextNodeRef - the context node for relative expressions etc
     * @param XPath - the xpath string to evaluate
     * @param parameters - parameters to bind in to the xpath expression
     * @param namespacePrefixResolver - prefix to namespace mappings
     * @param followAllParentLinks - if false ".." follows only the primary parent links, if true it follows all 
     * @return a list of all the child assoc relationships to the selected nodes
     */
    public List<NodeRef> selectNodes(
            NodeRef contextNodeRef,
            String XPath,
            QueryParameterDefinition[] parameters,
            NamespacePrefixResolver namespacePrefixResolver,
            boolean followAllParentLinks)
            throws InvalidNodeRefException, XPathException;

    /**
     * Select properties using an xpath expression 
     * 
     * @param contextNodeRef - the context node for relative expressions etc
     * @param XPath - the xpath string to evaluate
     * @param parameters - parameters to bind in to the xpath expression
     * @param namespacePrefixResolver - prefix to namespace mappings
     * @param followAllParentLinks - if false ".." follows only the primary parent links, if true it follows all 
     * @return a list of property values 
     */
    public List<Serializable> selectProperties(
            NodeRef contextNodeRef,
            String XPath,
            QueryParameterDefinition[] parameters,
            NamespacePrefixResolver namespacePrefixResolver,
            boolean followAllParentLinks)
            throws InvalidNodeRefException, XPathException;

    /**
     * Search for string pattern in both the node text (if present) and node properties
     * 
     * @param nodeRef the node to get
     * @param propertyQName the name of the property
     * @param googleLikePattern a Google-like pattern to search for in the property value
     * @return Returns true if the pattern could be found
     */
    public boolean contains(
            NodeRef nodeRef,
            QName propertyQName,
            String googleLikePattern)
            throws InvalidNodeRefException;
    
    /**
     * Search for string pattern in both the node text (if present) and node properties
     * 
     * @param nodeRef the node to get
     * @param propertyQName the name of the property
     * @param sqlLikePattern a SQL-like pattern to search for
     * @param includeFTS - include full textv serach matches in the like test
     * @return Returns true if the pattern could be found
     */
    public boolean like(
            NodeRef nodeRef,
            QName propertyQName,
            String sqlLikePattern,
            boolean includeFTS)
            throws InvalidNodeRefException;
}
