/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.sync.audit;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.alfresco.enterprise.repo.sync.audit.SyncEventHandler.AuditEventId;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

/**
 * @author Neil Mc Erlean
 * @since CloudSync
 */
public class SyncChangeEventImpl implements SyncChangeEvent
{
    private final Long entryId;
    private final String user;
    private final long time;
    private final Map<String, Serializable> values;
    
    public SyncChangeEventImpl(Long entryId, String user, long time, Map<String, Serializable> values)
    {
        this.entryId = entryId;
        this.user = user;
        this.time = time;
        this.values = Collections.unmodifiableMap(values);
    }
    
    @Override public Long getAuditId()
    {
        return entryId;
    }
    
    @Override public String getUser()
    {
        return this.user;
    }
    
    @Override public long getTime()
    {
        return this.time;
    }
    
    @Override public Map<String, Serializable> getValues()
    {
        return this.values;
    }
    
    @Override public AuditEventId getEventId()
    {
        return (AuditEventId) values.get(SyncEventHandler.PATH_TO_EVENT_ID_KEY);
    }
    
    @Override public String getSsdId()
    {
        return (String) values.get(SyncEventHandler.PATH_TO_SSDID_KEY);
    }
    
    @Override public NodeRef getNodeRef()
    {
        final String nodeRefString = (String) values.get(SyncEventHandler.PATH_TO_NODEREF_KEY);
        return new NodeRef(nodeRefString);
    }
    
    @Override public NodeRef getOtherNodeRef()
    {
        final String nodeRefString = (String) values.get(SyncEventHandler.PATH_TO_NODEREF_OTHER_KEY);
        return (nodeRefString != null ? new NodeRef(nodeRefString) : null);
    }
    
    @Override public QName getNodeType()
    {
        return (QName) values.get(SyncEventHandler.PATH_TO_NODETYPE_KEY);
    }
    
    @Override public QName getAspect()
    {
        return (QName) values.get(SyncEventHandler.PATH_TO_ASPECT_KEY);
    }
    
    @Override public Set<QName> getPropertyNames()
    {
        @SuppressWarnings("unchecked")
        Set<QName> result = (Set<QName>) values.get(SyncEventHandler.PATH_TO_PROPS_KEY);
        return result == null ? Collections.<QName>emptySet() : result;
    }
    
    public String toString()
    {
        return "SyncChangesEventImpl, entryId:" + entryId + ", user:" + user + ", eventId:" + getEventId() + ", nodeRef:" + getNodeRef();
    }
}