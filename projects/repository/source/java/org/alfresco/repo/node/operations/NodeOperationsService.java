/**
 * Created on May 5, 2005
 */
package org.alfresco.repo.node.operations;

import org.alfresco.repo.ref.NodeRef;
import org.alfresco.repo.ref.QName;

/**
 * Node operations service interface.
 * <p>
 * This interface provides methods to copy nodes within and across workspaces and to 
 * update the state of a node, with that of another node, within and across workspaces.
 * 
 * @author Roy Wetherall
 */
public interface NodeOperationsService
{
    /**
     * Creates a copy of the given node.
     * <p>
     * If the new node resides in a different workspace the new node will
     * have the same id.  
     * <p>
     * If the new node resides in the same workspace then
     * the new node will have the Copy aspect applied to it which will 
     * reference the origional node.     
     * <p>
     * The aspects applied to source node will also be applied to destination node 
     * and all the property value will be duplicated accordinly.  This is with the
     * exception of the apects that have been marked as having 'Non-Transferable State'.
     * In this case the aspect will be applied to the copy, but the properties will take
     * on the default values.
     * <p>
     * Child associations are copied onto the destination node.  If the child of 
     * copied association is not present in the destination workspace the child 
     * association is not copied.  This is unless is has been specfied that the 
     * children of the source node should also be copied.
     * <p>
     * Target associations are copied to the destination node.  If the target of the
     * association is not present in the destination workspace then the association is
     * not copied.
     * <p>
     * Source association are not copied.
     * 
     * @param sourceNodeRef             the node reference used as the source of the copy
     * @param destinationParent  		the intended parent of the new node
     * @param destinationAssocTypeQName the type of the new child assoc         
     * @param destinationQName 			the qualified name of the child association from the 
     *                                  parent to the new node
     * 
     * @return                          the new node reference
     */
    public NodeRef copy(
            NodeRef sourceNodeRef,            
            NodeRef destinationParent,
            QName destinationAssocTypeQName, 
            QName destinationQName, 
            boolean copyChildren);
    
    /**
     * By default children of the source node are not copied.
     * 
     * @see NodeCopyService#copy(NodeRef, NodeRef, QName, QName, boolean)
     * 
     * @param sourceNodeRef             the node reference used as the source of the copy
     * @param destinationParent  		the intended parent of the new node
     * @param destinationAssocTypeQName the type of the new child assoc         
     * @param destinationQName 			the qualified name of the child association from the 
     *                                  parent to the new node
     * @return                          the new node reference
     */
    public NodeRef copy(
            NodeRef sourceNodeRef,            
            NodeRef destinationParent,
            QName destinationAssocTypeQName, 
            QName destinationQName);
    
    // TODO
    // copyPropertiesOnly ??
    // copy with a set of node references specified ??
    
    /**
     * Copies the state of one node on top of another.
     * <p>
     * The state of destination node is overlayed with the state of the 
     * source node.  Any conflicts are resolved by setting the state to
     * that of the source node.
     * <p>
     * If data (for example an association) does not exist on the source
     * node, but does exist on the detination node this data is NOT deleted
     * from the destination node.
     * <p>
     * Child associations and target associations are updated on the destination
     * based on the current state of the source node.
     * <p>
     * If the node that either a child or target association points to on the source
     * node is not present in the destinations workspace then the association is not 
     * updated to the destination node.
     * <p>
     * All aspects found on the source node are applied to the destination node where 
     * missing.  The properties of the apects are updated accordingly except in the case
     * where the aspect has been marked as having 'Non-Transferable State'.  In this case 
     * aspect properties will take on the values already assigned to them in the
     * destination node. 
     * 
     * @param sourceNodeRef    the destination node reference
     * @param destinationNodeRef         the source node reference
     */
    public void copy(NodeRef sourceNodeRef, NodeRef destinationNodeRef);
    
    // TODO
    // update with includeChildren == true ?? not sure what this would mean ??
    // update with a set of nodes provided ??
    
}
