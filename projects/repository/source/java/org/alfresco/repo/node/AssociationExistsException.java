package org.alfresco.repo.node;

import org.alfresco.repo.ref.NodeRef;
import org.alfresco.repo.ref.QName;

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
    private QName qname;
    
    /**
     * @param sourceRef the source of the association
     * @param targetRef the target of the association
     * @param qname the qualified name of the association
     */
    public AssociationExistsException(NodeRef sourceRef, NodeRef targetRef, QName qname)
    {
        super();
        this.sourceRef = sourceRef;
        this.targetRef = targetRef;
        this.qname = qname;
    }

    public NodeRef getSourceRef()
    {
        return sourceRef;
    }

    public NodeRef getTargetRef()
    {
        return targetRef;
    }
    
    public QName getQName()
    {
        return qname;
    }
}
