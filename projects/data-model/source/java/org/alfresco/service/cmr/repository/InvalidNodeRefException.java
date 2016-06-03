package org.alfresco.service.cmr.repository;

import org.alfresco.api.AlfrescoPublicApi;

/**
 * Thrown when an operation cannot be performed because the <b>node</b> reference
 * no longer exists.
 * 
 * @author Derek Hulley
 */
@AlfrescoPublicApi
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
        this(msg, nodeRef, null);
    }

    public InvalidNodeRefException(String msg, NodeRef nodeRef, Throwable cause)
    {
        super(msg, cause);
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
