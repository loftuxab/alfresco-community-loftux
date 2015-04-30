/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.sync.transport;

import org.alfresco.enterprise.repo.sync.audit.SyncChangeEvent;
import org.alfresco.enterprise.repo.sync.deltas.AggregatedNodeChange.SsmnChangeType;
import org.alfresco.service.cmr.repository.NodeRef;

public interface AuditToken
{
    /**
     * Records the given {@link SyncChangeEvent} as applying to this Token.
     */
    public void record(SyncChangeEvent syncEvent, SsmnChangeType changeType);
    
    /**
     * Serializes the object into a JSON value
     */
    public Object asJSON();
    
    /**
     * Get list of auditIds
     */
    public long[] getAuditIds();
    
    public NodeRef getNodeRef();
    
    public NodeRef getOtherNodeRef();
    
    public SsmnChangeType getChangeType();
}
