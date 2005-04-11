package com.activiti.repo.node;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

import com.activiti.repo.ref.ChildAssocRef;
import com.activiti.repo.ref.EntityRef;
import com.activiti.repo.ref.NodeRef;
import com.activiti.repo.ref.Path;
import com.activiti.repo.ref.QName;

/**
 * Interface for public and internal <b>node</b> operations
 * 
 * @author Derek Hulley
 */
public interface NodeService
{
    /**
     * @see #createNode(NodeRef, QName, String, Map<String,String>)
     */
    public ChildAssocRef createNode(NodeRef parentRef,
            QName qname,
            String nodeType) throws InvalidNodeRefException;
    
    /**
     * Creates a new, non-abstract, real node as a primary child of the given parent node.
     * 
     * @param parentRef the parent node
     * @param qname the qualified name of the association
     * @param nodeType a predefined node type
     * @param properties optional map of properties to keyed by their qualified names
     * @return Returns a reference to the newly created child association
     * @throws InvalidNodeRefException if the parent reference is invalid
     */
    public ChildAssocRef createNode(NodeRef parentRef,
            QName qname,
            String nodeType,
            Map<QName, Serializable> properties) throws InvalidNodeRefException;
    
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
     * @return Returns the type of the node
     * @throws InvalidNodeRefException if the node could not be found
     */
    public String getType(NodeRef nodeRef) throws InvalidNodeRefException;
    
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
     * 
     * @param nodeRef
     * @param properties all the properties of the node keyed by their qualified names
     * @throws InvalidNodeRefException if the node could not be found
     */
    public void setProperties(NodeRef nodeRef, Map<QName, Serializable> properties) throws InvalidNodeRefException;
    
    /**
     * @param nodeRef
     * @param qname the fully qualified name of the property
     * @param propertyValue the value of the property
     * @throws InvalidNodeRefException if the node could not be found
     */
    public void setProperty(NodeRef nodeRef, QName qname, Serializable value) throws InvalidNodeRefException;
    
    /**
     * @param nodeRef the child node
     * @return Returns a collection of <code>NodeRef</code> instances
     * @throws InvalidNodeRefException if the node could not be found
     */
    public Collection<NodeRef> getParents(NodeRef nodeRef) throws InvalidNodeRefException;
    
    /**
     * @param nodeRef the parent node - must be a <b>container</b>
     * @return Returns a collection of <code>ChildAssocRef</code> instances
     * @throws InvalidNodeRefException if the node could not be found
     */
    public Collection<ChildAssocRef> getChildAssocs(NodeRef nodeRef) throws InvalidNodeRefException;
    
    /**
     * @param nodeRef
     * @return Returns Fetches the primary parent of the node unless it is a root node,
     *      in which case null is returned.
     * @throws InvalidNodeRefException if the node could not be found
     */
    public NodeRef getPrimaryParent(NodeRef nodeRef) throws InvalidNodeRefException;
    
    /**
     * 
     * @param sourceRef a reference to a <b>real</b> node
     * @param targetRef a reference to a node
     * @param qname the qualified name of the association
     * @throws InvalidNodeRefException if either of the nodes could not be found
     * @throws AssociationExistsException
     */
    public void createAssociation(NodeRef sourceRef, NodeRef targetRef, QName qname)
            throws InvalidNodeRefException, AssociationExistsException;
    
    /**
     * 
     * @param sourceRef the associaton source node
     * @param targetRef the association target node
     * @param qname the qualified name of the association
     * @throws InvalidNodeRefException if either of the nodes could not be found
     */
    public void removeAssociation(NodeRef sourceRef, NodeRef targetRef, QName qname)
            throws InvalidNodeRefException;
    
    /**
     * @param sourceRef the association source
     * @param qname the qualified name of the association
     * @return Returns a collection of <code>NodeRef</code> instances at the target end of the
     *      named association for which the given node is a source
     * @throws InvalidNodeRefException if the source node could not be found
     */
    public Collection<NodeRef> getAssociationTargets(NodeRef sourceRef, QName qname)
            throws InvalidNodeRefException;
    
    /**
     * @param targetRef the association target
     * @param qname the qualified name of the association
     * @return Returns a collection of <code>NodeRef</code> instances at the source of the
     *      named association for which the given node is a target
     * @throws InvalidNodeRefException
     */
    public Collection<NodeRef> getAssociationSources(NodeRef targetRef, QName qname)
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
}
