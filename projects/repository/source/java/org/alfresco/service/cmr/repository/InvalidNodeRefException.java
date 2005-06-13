package org.alfresco.service.cmr.repository;


/**
 * Thrown when an operation cannot be performed because the <b>node</b> reference
 * no longer exists.
 * 
 * @author Derek Hulley
 */
public class InvalidNodeRefException extends RuntimeException
{
    private static final long serialVersionUID = 3689345520586273336L;

    private NodeRef nodeRef;
    
    public InvalidNodeRefException(NodeRef nodeRef)
    {
        this(null, nodeRef);
    }

    public InvalidNodeRefException(String msg, NodeRef nodeRef)
    {
        super(msg);
        this.nodeRef = nodeRef;
    }

    /**
     * @return Returns the offending node reference
     */
    public NodeRef getNodeRef()
    {
        return nodeRef;
    }
}
