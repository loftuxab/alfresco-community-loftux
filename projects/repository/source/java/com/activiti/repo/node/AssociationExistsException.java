package com.activiti.repo.node;

import com.activiti.repo.ref.NodeRef;

/**
 * Thrown when an operation could not be performed because a named association already
 * exists between two nodes
 * 
 * @author Derek Hulley
 */
public class AssociationExistsException extends RuntimeException
{
    private static final long serialVersionUID = 3256440317824874800L;

    private NodeRef sourceRef;
    private NodeRef targetRef;
    private String assoc;
    
    /**
     * @param sourceRef the source of the association
     * @param targetRef the target of the association
     * @param name the name of the association
     */
    public AssociationExistsException(NodeRef sourceRef, NodeRef targetRef, String name)
    {
        super();
        this.sourceRef = sourceRef;
        this.targetRef = targetRef;
        this.assoc = name;
    }

    public NodeRef getSourceRef()
    {
        return sourceRef;
    }

    public NodeRef getTargetRef()
    {
        return targetRef;
    }
    
    public String getAssoc()
    {
        return assoc;
    }
}
