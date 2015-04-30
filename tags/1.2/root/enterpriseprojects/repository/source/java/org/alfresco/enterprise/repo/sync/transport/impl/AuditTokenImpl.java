/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.sync.transport.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.enterprise.repo.sync.audit.SyncChangeEvent;
import org.alfresco.enterprise.repo.sync.deltas.AggregatedNodeChange.SsmnChangeType;
import org.alfresco.enterprise.repo.sync.transport.AuditToken;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.util.EqualsHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * AuditToken
 * 
 * @since 4.1
 */
public class AuditTokenImpl implements AuditToken
{
    /**
     * The logger
     */
    private static Log logger = LogFactory.getLog(AuditTokenImpl.class);

    protected static final String JSON_NODEREF = "nodeRef";
    protected static final String JSON_OTHER_NODEREF = "otherNodeRef";
    protected static final String JSON_CHANGE_TYPE = "changeType";
    protected static final String JSON_AUDIT_IDS = "auditIds";
    
    private List<Long> auditIds = new ArrayList<Long>();
    
    private SsmnChangeType changeType;
    private NodeRef nodeRef;
    private NodeRef otherNodeRef;
    
    public AuditTokenImpl()
    {}
    public AuditTokenImpl(Object jsonValue)
    {
        if (jsonValue instanceof JSONObject)
        {
            parseJSONValue((JSONObject)jsonValue);
        }
        else if (jsonValue instanceof JSONArray || "[]".equals(jsonValue))
        {
            logger.warn("Invalid Audit Token JSON received: " + jsonValue);
        }
        else if (jsonValue == null || "{}".equals(jsonValue))
        {
            // There was no token, treat as an empty list
        }
        else
        {
            throw new IllegalArgumentException("Invalid JSON received, found " + jsonValue.getClass() + " =" + jsonValue);
        }
    }
    
    private void parseJSONValue(JSONObject jsonObj)
    {
        String str = (String)jsonObj.get(JSON_NODEREF);
        if (str != null)
        {
            nodeRef = new NodeRef(str);
        }
        str = (String)jsonObj.get(JSON_OTHER_NODEREF);
        if (str != null)
        {
            otherNodeRef = new NodeRef(str);
        }
        
        str = (String)jsonObj.get(JSON_CHANGE_TYPE);
        if (str != null)
        {
            changeType = SsmnChangeType.valueOf(str);
        }
        JSONArray arr = (JSONArray)jsonObj.get(JSON_AUDIT_IDS);
        if (arr != null)
        {
            for (Object o : arr)
            {
                auditIds.add((Long)o);
            }
        }
    }
    
    /**
     * Records the given {@link SyncChangeEvent} as applying
     *  to this Token.
     */
    public void record(SyncChangeEvent syncEvent, SsmnChangeType changeType)
    {
        auditIds.add(syncEvent.getAuditId());
        
        this.changeType = changeType;
        
        nodeRef = syncEvent.getNodeRef();
        otherNodeRef = syncEvent.getOtherNodeRef();
    }
    
    /**
     * Unit testing only - exact format may change over time!
     */
    public void record(long auditId)
    {
        auditIds.add(auditId);
    }
    
    public void setOtherNodeRef(NodeRef nodeRef)
    {
        otherNodeRef = nodeRef;
    }
    
    public long[] getAuditIds()
    {
        long[] ids = new long[auditIds.size()];
        for (int i=0; i<ids.length; i++)
        {
            ids[i] = auditIds.get(i);
        }
        return ids;
    }
    
    public SsmnChangeType getChangeType()
    {
        return changeType;
    }
    
    public NodeRef getNodeRef()
    {
        return nodeRef;
    }
    
    public NodeRef getOtherNodeRef()
    {
        return otherNodeRef;
    }
    
    /**
     * Serializes the object into a JSON value
     */
    @SuppressWarnings("unchecked")
    public JSONObject asJSON()
    {
        Map<String, Object> map = new HashMap<String, Object>(4);
        if (nodeRef != null) { map.put(JSON_NODEREF, nodeRef.toString()); }
        if (otherNodeRef != null) { map.put(JSON_OTHER_NODEREF, otherNodeRef.toString()); }
        if (changeType != null) { map.put(JSON_CHANGE_TYPE, changeType.toString()); }
        
        JSONArray json = new JSONArray();
        json.addAll(auditIds);
        map.put(JSON_AUDIT_IDS, json);
        
        return new JSONObject(map);
    }
    
    /**
     * Two AuditTokenImpl objects are equal if they contain
     *  the same Audit IDs, which must also be in the same order!
     */
    @Override public boolean equals(Object obj)
    {
        if (obj instanceof AuditTokenImpl)
        {
            AuditTokenImpl other = (AuditTokenImpl)obj;
            return (auditIds.equals(other.auditIds) &&
                    EqualsHelper.nullSafeEquals(changeType, other.changeType));
        }
        else
        {
            return false;
        }
    }
    
    @Override public int hashCode()
    {
        int hash = 0;
        
        if (auditIds != null)
        {
            hash += auditIds.hashCode();
        }
        if (changeType != null)
        {
            hash += 7 * changeType.hashCode();
        }
        
        return hash;
    }
    
    // debug only
    @Override
    public String toString()
    {
        return "AuditToken["+ asJSON().toString() +"]";
    }
}
