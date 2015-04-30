/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.sync.audit;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import org.alfresco.enterprise.repo.sync.SyncSetDefinition;
import org.alfresco.enterprise.repo.sync.audit.SyncEventHandler.AuditEventId;
import org.alfresco.repo.audit.AuditComponent;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;


/**
 * This interface defines the basic data which are captured for each 'sync audit event'.
 * 
 * @author Neil Mc Erlean
 * @since CloudSync
 */
public interface SyncChangeEvent
{
    /** Returns the id for this entry in the audit tables. {@link AuditComponent} */
    Long getAuditId();
    
    /** Return the user who caused this entry in the audit tables. */
    String getUser();
    
    /** Get the time at which this audit entry was created. */
    long getTime();
    
    /** Get the raw audit values for this entry */
    Map<String, Serializable> getValues();
    
    /** Convenience method to get the event type of this audit entry. */
    AuditEventId getEventId();
    
    /** Convenience method to get the {@link SyncSetDefinition#getId() Sync Set Definition ID}.*/
    String getSsdId();
    
    /**
     * Convenience method to get the NodeRef which was changed in some way thus triggering the audit entry.
     * Note that this may be <code>null</null> in some circumstances.
     */
    NodeRef getNodeRef();
    
    /**
     * Convenience method to get the "other" NodeRef - ie. source (if other is target) and target (if other is source)
     * Note that this may be <code>null</null> in some circumstances.
     */
    NodeRef getOtherNodeRef();
    
    /**
     * Convenience method to get the {@link QName type} of the audited node.
     */
    QName getNodeType();
    
    /**
     * Convenience method to get an aspect that was added or removed, if any.
     * @see #getEventId() to determine whether the aspect was added or removed.
     */
    QName getAspect();
    
    /**
     * Convenience method to get the property names of properties which were changed in some way.
     */
    Set<QName> getPropertyNames();
}