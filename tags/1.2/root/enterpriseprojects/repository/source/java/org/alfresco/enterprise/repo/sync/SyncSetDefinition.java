/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.sync;

import org.alfresco.service.cmr.remotecredentials.BaseCredentialsInfo;
import org.alfresco.service.cmr.repository.NodeRef;

/**
 * A simple POJO for {@link SyncModel#TYPE_SYNC_SET_DEFINITION sync set definition content types}.
 * 
 * @author Neil Mc Erlean
 * @since 4.1
 */
public final class SyncSetDefinition
{
    private final String sourceRepoId; // note: always the same within a single source repo (required by target repo dealing with multiple source repos)
    private final String guid;
    private NodeRef nodeRef;
    private String syncCreator;
    private BaseCredentialsInfo remoteCredentials;
    
    private String remoteTenantId;
    /** This is a String and not a NodeRef because it is a remote NodeRef. */
    private String targetFolderNodeRef;
    private boolean lockSourceCopy;
    private boolean includeSubFolders = false;
    private boolean isDeleteOnCloud = true;
    private boolean isDeleteOnPrem = false;
    
    
    public SyncSetDefinition(String guid, String sourceRepoId)
    {
        this(guid, sourceRepoId, null);
    }
    
    public SyncSetDefinition(String guid, String sourceRepoId, NodeRef nodeRef)
    {
        this.guid         = guid;
        this.sourceRepoId = sourceRepoId;
        this.nodeRef      = nodeRef;
    }
    
    public String getId()
    {
        return this.guid;
    }
    
    public String getSourceRepoId()
    {
        return this.sourceRepoId;
    }
    
    public NodeRef getNodeRef()
    {
        return this.nodeRef;
    }
    
    void setNodeRef(NodeRef nodeRef)
    {
        this.nodeRef = nodeRef;
    }
    
    public String getSyncCreator()
    {
        return this.syncCreator;
    }
    
    public void setSyncCreator(String syncCreator)
    {
        this.syncCreator = syncCreator;
    }
    
    public BaseCredentialsInfo getRemoteCredentials()
    {
        return this.remoteCredentials;
    }
    
    public void setRemoteCredentials(BaseCredentialsInfo remoteCredentials)
    {
        this.remoteCredentials = remoteCredentials;
    }
    
    public boolean getLockSourceCopy()
    {
        return this.lockSourceCopy;
    }
    
    public void setLockSourceCopy(boolean lockSourceCopy)
    {
        this.lockSourceCopy = lockSourceCopy;
    }
    
    public boolean getIncludeSubFolders()
    {
        return this.includeSubFolders;
    }
    
    public void setIncludeSubFolders(boolean includeSubFolders)
    {
        this.includeSubFolders = includeSubFolders;
    }
    
    public String getRemoteTenantId()
    {
        return this.remoteTenantId;
    }
    
    public void setRemoteTenantId(String remoteTenantId)
    {
        this.remoteTenantId = remoteTenantId;
    }

    public String getTargetFolderNodeRef()
    {
        return this.targetFolderNodeRef;
    }
    
    public void setTargetFolderNodeRef(String targetFolderNodeRef)
    {
        this.targetFolderNodeRef = targetFolderNodeRef;
    }
    
    @Override public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((guid == null) ? 0 : guid.hashCode());
        return result;
    }
    
    @Override public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SyncSetDefinition other = (SyncSetDefinition) obj;
        if (guid == null)
        {
            if (other.guid != null)
                return false;
        } else if (!guid.equals(other.guid))
            return false;
        return true;
    }
    
    // debug only
    @Override public String toString()
    {
        StringBuilder sb = new StringBuilder(80);
        sb.append("SSD")
          .append("[id=").append(getId())
          .append(", srcRepoId=").append(getSourceRepoId())
          .append(", nodeRef=").append(getNodeRef())
          .append(", syncCreator=").append(getSyncCreator())
          .append(", targetUserName=").append((getRemoteCredentials() != null ? getRemoteCredentials().getRemoteUsername() : "null"))
          .append(", targetTenantDomain=").append(getRemoteTenantId())
          .append(", targetFolderNodeRef=").append(getTargetFolderNodeRef())
          .append(", includeSubFolders=").append(getIncludeSubFolders())
          .append(", isDeleteOnCloud=").append(isDeleteOnCloud())
          .append(", isDeleteOnPrem=").append(isDeleteOnPrem())
          .append(", lockSourceCopy=").append(getLockSourceCopy());
        
        sb.append("]");
        return sb.toString();
    }

	public boolean isDeleteOnCloud() {
		return isDeleteOnCloud;
	}
	
	public boolean isDeleteOnPrem() {
		return isDeleteOnPrem;
	}

	public void setDeleteOnCloud(boolean isDeleteOnCloud) {
		this.isDeleteOnCloud = isDeleteOnCloud;
	}

	public void setDeleteOnPrem(boolean isDeleteOnPrem) {
		this.isDeleteOnPrem = isDeleteOnPrem;
	}
	
	// MER - Work around - cant't get "is" methods working in freemarker
	public boolean getDeleteOnCloudFlag() {
		return isDeleteOnCloud;
	}
	
	public boolean getDeleteOnPremFlag() {
		return isDeleteOnPrem;
	}

}
