package org.alfresco.service.cmr.repository;

import org.alfresco.api.AlfrescoPublicApi;
import org.alfresco.service.namespace.QName;

/**
 * Thrown when an operation could not be performed because a named association already
 * exists between two nodes
 * 
 * @author Derek Hulley
 */
@AlfrescoPublicApi
public class AssociationExistsException extends RuntimeException
{
    private static final long serialVersionUID = 3256440317824874800L;

    private Long sourceNodeId;
    private Long targetNodeId;
    private QName qname;
    
    /**
     * @see #AssociationExistsException(Long, Long, org.alfresco.service.namespace.QName, Throwable)
     */
    public AssociationExistsException(Long sourceNodeId, Long targetNodeId, QName qname)
    {
        super();
        this.sourceNodeId = sourceNodeId;
        this.targetNodeId = targetNodeId;
        this.qname = qname;
    }

    /**
     * @param sourceNodeId      the source of the association
     * @param targetNodeId      the target of the association
     * @param qname             the qualified name of the association
     * @param cause a causal exception
     */
    public AssociationExistsException(Long sourceNodeId, Long targetNodeId, QName qname, Throwable cause)
    {
        super(cause);
        this.sourceNodeId = sourceNodeId;
        this.targetNodeId = targetNodeId;
        this.qname = qname;
    }

    public Long getSourceNodeId()
    {
        return sourceNodeId;
    }

    public Long getTargetNodeId()
    {
        return targetNodeId;
    }
    
    public QName getQName()
    {
        return qname;
    }
}
