package org.alfresco.service.cmr.repository;

import org.alfresco.repo.domain.ChildAssoc;

/**
 * Thrown when a cyclic parent-child relationship is detected.
 * 
 * @author Derek Hulley
 */
public class CyclicChildRelationshipException extends RuntimeException
{
    private static final long serialVersionUID = 3545794381924874036L;

    private ChildAssoc assoc;
    
    public CyclicChildRelationshipException(String msg, ChildAssoc assoc)
    {
        super(msg);
        this.assoc = assoc;
    }

    public ChildAssoc getAssoc()
    {
        return assoc;
    }
}
