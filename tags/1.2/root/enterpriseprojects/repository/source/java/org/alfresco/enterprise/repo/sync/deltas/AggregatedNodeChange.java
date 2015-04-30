/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.sync.deltas;

import static org.alfresco.util.collections.CollectionUtils.nullSafeAppend;
import static org.alfresco.util.collections.CollectionUtils.nullSafeMerge;

import org.alfresco.enterprise.repo.sync.audit.SyncChangeEvent;
import org.alfresco.enterprise.repo.sync.audit.SyncEventHandler.AuditEventId;
import org.alfresco.enterprise.repo.sync.transport.SyncNodeChangesInfo;
import org.alfresco.enterprise.repo.sync.transport.impl.SyncNodeChangesInfoImpl;
import org.alfresco.error.AlfrescoRuntimeException;

/**
 * @author Neil Mc Erlean
 * @since 4.1
 */
public class AggregatedNodeChange
{
    /**
     * This enum identifies the type of change for a single node that is being pushed/pulled.
     * Some combinations are illegal and should be considered programming errors e.g. 2 {@link CREATE} changes on the same node does not make sense.
     */
    public enum SsmnChangeType
    {
        CREATE, UPDATE, REMOVE, DELETE, MOVE;
        
        public SsmnChangeType append(SsmnChangeType add)
        {
            SsmnChangeType result = this;
            
            // CREATE followed by UPDATE can be treated as a single CREATE, which will transport all current data for the node.
            if (CREATE.equals(this) && UPDATE.equals(add))
            {
                result = CREATE;
            }
            // One UPDATE followed by another UPDATE can be treated as a single UPDATE.
            else if (UPDATE.equals(this) && UPDATE.equals(add))
            {
                result = UPDATE;
            }
            else if (DELETE.equals(add))
            {
                result = DELETE;
            }
            else
            {
                illegalCombination(add);
            }
            return result;
        }
        
        private void illegalCombination(SsmnChangeType add)
        {
            throw new AlfrescoRuntimeException("Illegal " + SsmnChangeType.class.getSimpleName() + " combination: " + this + " + " + add);
        }
    }
    
    private final SyncNodeChangesInfoImpl snci;
    private SsmnChangeType changeType;
    
    public AggregatedNodeChange(SyncNodeChangesInfoImpl firstSnci, AuditEventId eventId)
    {
        this.snci = firstSnci;
        this.changeType = getChangeType(eventId);
    }
    
    public SsmnChangeType getChangeType()
    {
        return this.changeType;
    }

    public static SsmnChangeType getChangeType(AuditEventId eventId)
    {
        SsmnChangeType result = null;
        if (eventId == AuditEventId.SSD_TO_DELETE)
        {
            // This is a programming error if it happens.
            throw new AlfrescoRuntimeException("Illegal " + AuditEventId.class.getSimpleName() + ": " + eventId);
        }
        else if (eventId == AuditEventId.SSMN_ADDED)
        {
            result = SsmnChangeType.CREATE;
        }
        else if (eventId == AuditEventId.SSMN_REMOVED)
        {
            result = SsmnChangeType.REMOVE;
        }
        else if (eventId == AuditEventId.SSMN_DELETED)
        {
            result = SsmnChangeType.DELETE;
        }
        else
        {
            result = SsmnChangeType.UPDATE;
        }
        return result;
    }
    
    /**
     * This method checks whether it is possible to append a change event of the given type onto the existing
     * aggregated change list.
     */
    public boolean canAppend(SyncNodeChangesInfo newSnci, AuditEventId eventId)
    {
        // This is a programming error if it happens.
        if (eventId == AuditEventId.SSD_TO_DELETE)
        {
            throw new AlfrescoRuntimeException("Illegal " + AuditEventId.class.getSimpleName() + ": " + eventId);
        }
        
        // You cannot append an SSMN_ADD onto anything else.
        return !( (eventId == AuditEventId.SSMN_ADDED && this.changeType != null) &&
                  // or have an SSMN_DELETE be followed by anything
                  (this.changeType == SsmnChangeType.DELETE) &&
                  // and the SSD cannot change
                  this.snci.getSyncSetGUID().equals(newSnci.getSyncSetGUID()) &&
                  // and we only combine changes on one NodeRef
                  this.snci.getLocalNodeRef().equals(newSnci.getLocalNodeRef()) );
    }
    
    public boolean append(SyncNodeChangesInfoImpl newSnci, SyncChangeEvent event)
    {
        if ( !canAppend(newSnci, event.getEventId())) { return false; }
        
        try
        {
            this.changeType = this.changeType.append(getChangeType(event.getEventId()));
        }
        catch (AlfrescoRuntimeException appendFailed) { return false; }
        
        this.snci.setAspectsAdded(nullSafeMerge(snci.getAspectsAdded(), newSnci.getAspectsAdded(), true));
        this.snci.setAspectsRemoved(nullSafeMerge(snci.getAspectsRemoved(), newSnci.getAspectsRemoved(), true));
        this.snci.setContentUpdates(nullSafeMerge(snci.getContentUpdates(), newSnci.getContentUpdates(), true));
        this.snci.setPropertyUpdates(nullSafeMerge(snci.getPropertyUpdates(), newSnci.getPropertyUpdates(), true));
        this.snci.setLocalAuditIds(nullSafeAppend(snci.getLocalAuditIds(), newSnci.getLocalAuditIds(), true));
        this.snci.getAuditToken().record(event, this.changeType);
        
        return true;
    }
    
    public SyncNodeChangesInfo getSyncNodeChangesInfo()
    {
        return this.snci;
    }
}
