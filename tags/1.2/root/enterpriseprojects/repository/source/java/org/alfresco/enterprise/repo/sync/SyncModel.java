/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.sync;

import org.alfresco.service.namespace.QName;

/**
 * Utility interface for the syncModel.xml
 * 
 * @author Neil Mc Erlean
 * @since CloudSync
 */
public interface SyncModel
{
    /** Sync Model URI */
    static final String SYNC_MODEL_1_0_URI = "http://www.alfresco.org/model/sync/1.0";
    
    static final QName TYPE_SYNC_SET_DEFINITION            = QName.createQName(SYNC_MODEL_1_0_URI, "syncSetDefinition");
    static final QName PROP_SOURCE_REPO_ID                 = QName.createQName(SYNC_MODEL_1_0_URI, "sourceRepoId");
    static final QName PROP_SYNC_GUID                      = QName.createQName(SYNC_MODEL_1_0_URI, "syncGuid");
    static final QName PROP_SYNC_SET_IS_LOCKED_ON_PREMISE  = QName.createQName(SYNC_MODEL_1_0_URI, "syncSetIsLockedOnPremise");
    static final QName PROP_TARGET_NETWORK_ID              = QName.createQName(SYNC_MODEL_1_0_URI, "targetNetworkId");
    static final QName PROP_TARGET_ROOT_FOLDER             = QName.createQName(SYNC_MODEL_1_0_URI, "targetRootFolder");
    static final QName PROP_SYNC_CREATOR_USERNAME            = QName.createQName(SYNC_MODEL_1_0_URI, "syncCreatorUsername");
    static final QName PROP_SYNC_SET_INCLUDE_SUBFOLDERS    = QName.createQName(SYNC_MODEL_1_0_URI, "includeSubFolders");
    
    static final QName PROP_SYNC_SET_IS_DELETE_ON_CLOUD    = QName.createQName(SYNC_MODEL_1_0_URI, "isDeleteOnCloud");
    static final QName PROP_SYNC_SET_IS_DELETE_ON_PREM    = QName.createQName(SYNC_MODEL_1_0_URI, "isDeleteOnPrem");
    
    static final QName ASSOC_SYNC_MEMBERS                  = QName.createQName(SYNC_MODEL_1_0_URI, "members");
    
    /**
     * Applied to a node which is a member of a sync set
     */
    static final QName ASPECT_SYNC_SET_MEMBER_NODE         = QName.createQName(SYNC_MODEL_1_0_URI, "syncSetMemberNode");
    static final QName PROP_OTHER_NODEREF_STRING           = QName.createQName(SYNC_MODEL_1_0_URI, "otherNodeRefString");
    static final QName PROP_REMOTE_MODIFIED                = QName.createQName(SYNC_MODEL_1_0_URI, "remoteModified");
    static final QName PROP_REMOTE_MODIFIED_BY             = QName.createQName(SYNC_MODEL_1_0_URI, "remoteModifiedBy");
    static final QName PROP_DIRECT_SYNC                    = QName.createQName(SYNC_MODEL_1_0_URI, "directSync");
    static final QName PROP_SYNC_OWNER                     = QName.createQName(SYNC_MODEL_1_0_URI, "syncOwner");
    static final QName PROP_SYNC_REQUESTED                 = QName.createQName(SYNC_MODEL_1_0_URI, "syncRequested");
    static final QName PROP_SYNC_TIME                      = QName.createQName(SYNC_MODEL_1_0_URI, "syncTime");
    static final QName PROP_SYNC_LOCK                      = QName.createQName(SYNC_MODEL_1_0_URI, "syncLock");
    
    /**
     * Applied to a node that may be deleted on prem
     */
    static final QName ASPECT_DELETE_ON_PREM			   = QName.createQName(SYNC_MODEL_1_0_URI, "deleteOnPrem");
    
    /**
     * Applied to a node which has been successfully synchronised
     */
    static final QName ASPECT_SYNCED = QName.createQName(SYNC_MODEL_1_0_URI, "synced");
    static final QName PROP_SYNCED_THIS_VERSION_LABEL    = QName.createQName(SYNC_MODEL_1_0_URI, "thisVersionLabel");
    static final QName PROP_SYNCED_OTHER_VERSION_LABEL   = QName.createQName(SYNC_MODEL_1_0_URI, "remoteVersionLabel");
    
    /**
     * Applied to a node which has failed to sync.
     */
    static final QName ASPECT_SYNC_FAILED = QName.createQName(SYNC_MODEL_1_0_URI, "failed");
    static final QName PROP_SYNCED_FAILED_CODE   = QName.createQName(SYNC_MODEL_1_0_URI, "errorCode");
    static final QName PROP_SYNCED_FAILED_DETAILS   = QName.createQName(SYNC_MODEL_1_0_URI, "errorDetails");
    static final QName PROP_SYNCED_FAILED_TIME   = QName.createQName(SYNC_MODEL_1_0_URI, "errorTime");

}
