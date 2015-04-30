/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.sync.transport.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.enterprise.repo.sync.transport.AuditToken;
import org.alfresco.enterprise.repo.sync.transport.CloudSyncContent;
import org.alfresco.enterprise.repo.sync.transport.SyncNodeChangesInfo;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

/**
 * An implementation of {@link SyncNodeChangesInfo}
 *
 * TODO CheckSums
 *  
 * @author Nick Burch
 * @since 4.1
 */
public class SyncNodeChangesInfoImpl implements SyncNodeChangesInfo
{
    private NodeRef localNodeRef;
    private NodeRef remoteNodeRef;
    private String localPath;
    private String remotePath;
    private NodeRef localParentNodeRef;
    private NodeRef remoteParentNodeRef;
    private String syncSetGUID;
    private QName type;
    private Boolean directSync = false;
    
    private Date localModifiedAt;
    private Date remoteModifiedAt;
    private String localVersionLabel;
    private String remoteVersionLabel;
    private Set<QName> aspectsAdded                 = null;
    private Set<QName> aspectsRemoved               = null;
    private Map<QName,Serializable> propertyUpdates = null;
    private Map<QName,CloudSyncContent> contentUpdates = null;
    private List<Long> localAuditIds                = new ArrayList<Long>();
    private AuditToken auditToken                   = new AuditTokenImpl();
    
    public SyncNodeChangesInfoImpl(SyncNodeChangesInfo other)
    {
        this.localNodeRef = other.getLocalNodeRef();
        this.remoteNodeRef = other.getRemoteNodeRef();
        this.syncSetGUID = other.getSyncSetGUID();
        this.type = other.getType();
        this.localPath = other.getLocalPath();
        this.remotePath = other.getRemotePath();
        this.localParentNodeRef = other.getLocalParentNodeRef();
        this.remoteParentNodeRef = other.getRemoteParentNodeRef();
        this.directSync = other.getDirectSync();
        
        this.localModifiedAt = other.getLocalModifiedAt();
        this.remoteModifiedAt = other.getRemoteModifiedAt();
        this.localVersionLabel = other.getLocalVersionLabel();
        this.remoteVersionLabel = other.getRemoteVersionLabel();
        this.aspectsAdded  = other.getAspectsAdded();
        this.aspectsRemoved = other.getAspectsRemoved();
        this.propertyUpdates = other.getPropertyUpdates();
        this.contentUpdates = other.getContentUpdates();
        this.localAuditIds  = other.getLocalAuditIds();
        this.auditToken = other.getAuditToken();
    
    }
    
    public SyncNodeChangesInfoImpl(NodeRef localNodeRef, NodeRef remoteNodeRef,
                                   String syncSetGUID, QName type)
    {
        this.localNodeRef = localNodeRef;
        this.remoteNodeRef = remoteNodeRef;
        this.syncSetGUID = syncSetGUID;
        this.type = type;
    }
    
    public NodeRef getLocalNodeRef()
    {
        return localNodeRef;
    }
    public NodeRef getRemoteNodeRef()
    {
        return remoteNodeRef;
    }
    public void setRemoteNodeRef(NodeRef nodeRef)
    {
        this.remoteNodeRef = nodeRef;
    }
    
    public String getLocalPath()
    {
        return localPath;
    }
    public void setLocalPath(String localPath)
    {
        this.localPath = localPath;
    }

    public String getRemotePath()
    {
        return remotePath;
    }
    public void setRemotePath(String remotePath)
    {
        this.remotePath = remotePath;
    }
    public void setLocalNodeRef(NodeRef localNodeRef)
    {
        this.localNodeRef = localNodeRef;
    }
    public NodeRef getLocalParentNodeRef()
    {
        return localParentNodeRef;
    }
    public void setLocalParentNodeRef(NodeRef localParentNodeRef)
    {
        this.localParentNodeRef = localParentNodeRef;
    }
    
    public NodeRef getRemoteParentNodeRef()
    {
        return remoteParentNodeRef;
    }
    public void setRemoteParentNodeRef(NodeRef remoteParentNodeRef)
    {
        this.remoteParentNodeRef = remoteParentNodeRef;
    }

    public String getSyncSetGUID()
    {
        return syncSetGUID;
    }
    
    public void setSyncSetGUID(String syncSetGUID)
    {
        this.syncSetGUID = syncSetGUID;
    }

    public QName getType()
    {
        return type;
    }
    
    public void setType(QName type)
    {
        if (this.type == null) { this.type = type; }
    }

    public Date getLocalModifiedAt()
    {
        return localModifiedAt;
    }
    public void setLocalModifiedAt(Date localModifiedAt)
    {
        this.localModifiedAt = localModifiedAt;
    }

    public Date getRemoteModifiedAt()
    {
        return remoteModifiedAt;
    }
    public void setRemoteModifiedAt(Date remoteModifiedAt)
    {
        this.remoteModifiedAt = remoteModifiedAt;
    }

    public String getLocalVersionLabel()
    {
        return localVersionLabel;
    }
    public void setLocalVersionLabel(String localVersionLabel)
    {
        this.localVersionLabel = localVersionLabel;
    }

    public String getRemoteVersionLabel()
    {
        return remoteVersionLabel;
    }
    public void setRemoteVersionLabel(String remoteVersionLabel)
    {
        this.remoteVersionLabel = remoteVersionLabel;
    }

    public Set<QName> getAspectsAdded()
    {
        return aspectsAdded;
    }
    public void setAspectsAdded(Set<QName> aspectsAdded)
    {
        this.aspectsAdded = aspectsAdded;
    }

    public Set<QName> getAspectsRemoved()
    {
        return aspectsRemoved;
    }
    public void setAspectsRemoved(Set<QName> aspectsRemoved)
    {
        this.aspectsRemoved = aspectsRemoved;
    }

    public Map<QName, Serializable> getPropertyUpdates()
    {
        return propertyUpdates;
    }
    public void setPropertyUpdates(Map<QName, Serializable> propertyUpdates)
    {
        this.propertyUpdates = propertyUpdates;
    }

    public Map<QName,CloudSyncContent> getContentUpdates()
    {
        return contentUpdates;
    }
    public void setContentUpdates(Map<QName,CloudSyncContent> contentUpdates)
    {
        this.contentUpdates = contentUpdates;
    }
    
    public List<Long> getLocalAuditIds()
    {
        return this.localAuditIds;
    }
    
    public void setLocalAuditIds(List<Long> auditLIds)
    {
        this.localAuditIds = auditLIds;
    }

    public AuditToken getAuditToken()
    {
        return this.auditToken;
    }
    
    public void setAuditToken(AuditToken auditToken)
    {
        this.auditToken = auditToken;
    }
    
    public Boolean getDirectSync()
    {
        return this.directSync;
    }
    
    public void setDirectSync(Boolean directSync)
    {
        this.directSync = directSync;
    }
    
    // debug only
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder(80);
        sb.append("SNCI")
          .append("[ssdId=").append(getSyncSetGUID())
          .append(", type=").append(getType())
          .append(", localNodeRef=").append(getLocalNodeRef())
          .append(", localParentNodeRef=").append(getLocalParentNodeRef())
          .append(", remoteNodeRef=").append(getRemoteNodeRef())
          .append(", remoteParentNodeRef=").append(getRemoteParentNodeRef())
          .append(", auditToken=").append(getAuditToken())
          .append("]");
        return sb.toString();
    }

}
