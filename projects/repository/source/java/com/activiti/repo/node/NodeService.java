package com.activiti.repo.node;

import java.util.Map;

import com.activiti.repo.ref.NodeRef;

/**
 * Interface for public and internal <b>node</b> operations
 * 
 * @author derekh
 */
public interface NodeService
{
    /**
     * @see #createNode(NodeRef, String, String, Map)
     */
    public NodeRef createNode(NodeRef parentRef, String name, String nodeType) throws InvalidNodeRefException;
    
    /**
     * Creates a new, non-abstract, real node as a primary child of the given parent node.
     * 
     * @param parentRef the parent node
     * @param name the name of the child association between the parent and the new child
     * @param nodeType a predefined node type
     * @param properties optional map of properties to assign to the node
     * @return Returns a reference to the newly created node
     * @throws InvalidNodeRefException if the parent reference is invalid
     */
    public NodeRef createNode(NodeRef parentRef, String name, String nodeType, Map properties) throws InvalidNodeRefException;
    
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
     * @param name the name of the association
     * @throws InvalidNodeRefException if the parent or child nodes could not be found
     */
    public void addChild(NodeRef parentRef, NodeRef childRef, String name) throws InvalidNodeRefException;
    
    /**
     * Severs all parent-child relationships between two nodes.  A cascade delete to the child may occur as a result.
     * 
     * @param parentRef the parent end of the association
     * @param childRef the child end of the association
     * @return Returns true if the child node was cascade deleted, otherwise false
     * @throws InvalidNodeRefException if the parent or child nodes could not be found
     */
    public void removeChild(NodeRef parentRef, NodeRef childRef) throws InvalidNodeRefException;

    /**
     * 
     * @param parentRef the parent of the associations to remove
     * @param name the name of the association to remove
     * @return Returns true if the chi
     * @throws InvalidNodeRefException if the node could not be found
     */
    public void removeChild(NodeRef parentRef, String name) throws InvalidNodeRefException;
    
    /**
     * @param nodeRef
     * @return Returns the type of the node
     * @throws InvalidNodeRefException if the node could not be found
     */
    public String getType(NodeRef nodeRef) throws InvalidNodeRefException;
    
    /**
     * @param nodeRef
     * @return Returns all properties
     * @throws InvalidNodeRefException if the node could not be found
     */
    public Map getProperties(NodeRef nodeRef) throws InvalidNodeRefException;
    
    /**
     * 
     * @param nodeRef
     * @param properties all the properties of the node
     * @throws InvalidNodeRefException if the node could not be found
     */
    public void setProperties(NodeRef nodeRef, Map properties) throws InvalidNodeRefException;
}
