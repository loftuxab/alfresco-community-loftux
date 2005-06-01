package org.alfresco.repo.node;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.repo.ref.ChildAssocRef;
import org.alfresco.repo.ref.EntityRef;
import org.alfresco.repo.ref.NamespacePrefixResolver;
import org.alfresco.repo.ref.NodeAssocRef;
import org.alfresco.repo.ref.NodeRef;
import org.alfresco.repo.ref.Path;
import org.alfresco.repo.ref.QName;
import org.alfresco.repo.ref.StoreRef;
import org.alfresco.repo.ref.qname.QNamePattern;
import org.alfresco.repo.search.QueryParameterDefinition;

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
    public ChildAssocRef createNode(NodeRef parentRef,
            QName assocTypeQName,
            QName assocQName,
            QName nodeTypeQName)
            throws InvalidNodeRefException, InvalidNodeTypeException, PropertyException;
    
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
     * @throws InvalidNodeTypeException if the node type reference is not recognised
     * @throws PropertyException if a mandatory property was not set
     * 
     * @see org.alfresco.repo.dictionary.DictionaryService
     */
    public ChildAssocRef createNode(NodeRef parentRef,
            QName assocTypeQName,
            QName assocQName,
            QName nodeTypeQName,
            Map<QName, Serializable> properties)
            throws InvalidNodeRefException, InvalidNodeTypeException, PropertyException;
    
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
    public ChildAssocRef moveNode(
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
     * @see org.alfresco.repo.dictionary.DictionaryService
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
     * @see org.alfresco.repo.dictionary.DictionaryService#getAspect(QName)
     * @see org.alfresco.repo.dictionary.ClassDefinition#getProperties()
     */
    public void addAspect(NodeRef nodeRef,
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
     * @param qname the qualified name of the association
     * @return Returns a reference to the newly created child association
     * @throws InvalidNodeRefException if the parent or child nodes could not be found
     */
    public ChildAssocRef addChild(NodeRef parentRef,
            NodeRef childRef,
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
     * Removes named child associations and deletes the children where the association
     * was the primary association, i.e. the one with which the child node was created.
     * 
     * @param parentRef the parent of the associations to remove
     * @param qname the qualified name of the association
     * @return Returns a collection of references to all deleted entities
     * @throws InvalidNodeRefException if the node could not be found
     */
    public Collection<EntityRef> removeChildren(NodeRef parentRef, QName qname) throws InvalidNodeRefException;
    
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
     * Use this method to remove unwanted properties.  The properties given must still
     * fulfill the requirements of the class and aspects relevant to the node.
     * <p>
     * <b>NOTE:</b> Null values are not allowed.
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
     * <b>NOTE:</b> Null values are not allowed.
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
    public List<ChildAssocRef> getParentAssocs(NodeRef nodeRef) throws InvalidNodeRefException;
    
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
     * @see org.alfresco.repo.ref.qname.RegexQNamePattern#MATCH_ALL
     */
    public List<ChildAssocRef> getParentAssocs(NodeRef nodeRef, QNamePattern qnamePattern)
            throws InvalidNodeRefException;
    
    /**
     * @param nodeRef the parent node - usually a <b>container</b>
     * @return Returns a collection of <code>ChildAssocRef</code> instances.  If the
     *      node is not a <b>container</b> then the result will be empty.
     * @throws InvalidNodeRefException if the node could not be found
     * 
     * @see #getChildAssocs(NodeRef, QNamePattern)
     */
    public List<ChildAssocRef> getChildAssocs(NodeRef nodeRef) throws InvalidNodeRefException;
    
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
     * @see org.alfresco.repo.ref.qname.RegexQNamePattern#MATCH_ALL
     */
    public List<ChildAssocRef> getChildAssocs(NodeRef nodeRef, QNamePattern qnamePattern)
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
    public ChildAssocRef getPrimaryParent(NodeRef nodeRef) throws InvalidNodeRefException;
    
    /**
     * 
     * @param sourceRef a reference to a <b>real</b> node
     * @param targetRef a reference to a node
     * @param assocTypeQName the qualified name of the association type
     * @return Returns a reference to the new association
     * @throws InvalidNodeRefException if either of the nodes could not be found
     * @throws AssociationExistsException
     */
    public NodeAssocRef createAssociation(NodeRef sourceRef, NodeRef targetRef, QName assocTypeQName)
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
     * @see org.alfresco.repo.ref.qname.RegexQNamePattern#MATCH_ALL
     */
    public List<NodeAssocRef> getTargetAssocs(NodeRef sourceRef, QNamePattern qnamePattern)
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
     * @see org.alfresco.repo.ref.qname.RegexQNamePattern#MATCH_ALL
     */
    public List<NodeAssocRef> getSourceAssocs(NodeRef targetRef, QNamePattern qnamePattern)
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
     * @param contextNode - the context node for relative expressions etc
     * @param XPath - the xpath string to evaluate
     * @param parameters - parameters to bind in to the xpath expression
     * @param namespacePrefixResolver - prefix to namespace mappings
     * @param followAllParentLinks - if false ".." follows only the primary parent links, if true it follows all 
     * @return a list of all the child assoc relationships to the selected nodes
     */
    public List<ChildAssocRef> selectNodes(NodeRef contextNode, String XPath, QueryParameterDefinition[] parameters, NamespacePrefixResolver namespacePrefixResolver, boolean followAllParentLinks);

    /**
     * Select properties using an xpath expression 
     * 
     * @param contextNode - the context node for relative expressions etc
     * @param XPath - the xpath string to evaluate
     * @param parameters - parameters to bind in to the xpath expression
     * @param namespacePrefixResolver - prefix to namespace mappings
     * @param followAllParentLinks - if false ".." follows only the primary parent links, if true it follows all 
     * @return a list of property values 
     * TODO: Should be returning a property object 
     */
    public List<Serializable> selectProperties(NodeRef contextNode, String XPath, QueryParameterDefinition[] parameters, NamespacePrefixResolver namespacePrefixResolver, boolean followAllParentLinks);

    public boolean like(NodeRef nodeRef, QName property, String sqlLikePattern);
    
    public boolean contains(NodeRef nodeRef, QName property, String googleLikePattern);
}
