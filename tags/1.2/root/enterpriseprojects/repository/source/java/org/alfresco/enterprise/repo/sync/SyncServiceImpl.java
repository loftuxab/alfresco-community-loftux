/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.sync;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.alfresco.enterprise.repo.sync.SyncNodeException.SyncNodeExceptionType;
import org.alfresco.enterprise.repo.sync.audit.SyncAuditService;
import org.alfresco.enterprise.repo.sync.audit.SyncChangeEvent;
import org.alfresco.enterprise.repo.sync.deltas.AggregatedNodeChange;
import org.alfresco.enterprise.repo.sync.deltas.AggregatedNodeChange.SsmnChangeType;
import org.alfresco.enterprise.repo.sync.deltas.SsmnChangeManagement;
import org.alfresco.enterprise.repo.sync.transport.CloudSyncContent;
import org.alfresco.enterprise.repo.sync.transport.SyncNodeChangesInfo;
import org.alfresco.enterprise.repo.sync.transport.impl.SyncNodeChangesInfoImpl;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.policy.BehaviourFilter;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.permissions.AccessDeniedException;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.repo.version.VersionModel;
import org.alfresco.service.cmr.coci.CheckOutCheckInService;
import org.alfresco.service.cmr.lock.LockService;
import org.alfresco.service.cmr.lock.LockStatus;
import org.alfresco.service.cmr.lock.LockType;
import org.alfresco.service.cmr.model.FileExistsException;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.InvalidNodeRefException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.datatype.DefaultTypeConverter;
import org.alfresco.service.cmr.version.VersionService;
import org.alfresco.service.cmr.version.VersionType;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.ParameterCheck;
import org.alfresco.util.PropertyCheck;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.surf.util.I18NUtil;

/**
 * Sync Service Implementation
 * 
 * @author Neil Mc Erlean, janv, mrogers
 * @since 4.1
 */
public class SyncServiceImpl implements SyncService
{
    private static final Log logger = LogFactory.getLog(SyncServiceImpl.class);
    
    private NodeService nodeService;
    private FileFolderService fileFolderService;
    private CheckOutCheckInService cociService;
    private SyncAuditService syncAuditService;
    private SyncAdminService syncAdminService;
    private SyncChangeMonitor syncChangeMonitor;
    private SsmnChangeManagement ssmnChangeManagement;
    private ContentService contentService;
    private BehaviourFilter behaviourFilter;
    private VersionService versionService;
    private RetryingTransactionHelper retryingTransactionHelper;
    private LockService lockService;
    
    // Some URL constants used when talking to the Cloud.
    public static final String PARAM_SSD_ID = "ssdId";
    public static final String PARAM_SOURCE_REPO_ID = "sourceRepoId";
    public static final String PARAM_TARGET_FOLDER_NODEREF = "targetFolderNodeRef";
    public static final String PARAM_INCLUDE_SUBFOLDERS = "includeSubFolders";
    public static final String PARAM_IS_DELETE_ON_CLOUD = "isDeleteOnCloud";
    public static final String PARAM_IS_DELETE_ON_PREM = "isDeleteOnPrem";
     
    public static final String SYNC_AUDIT_MESSAGE_KEY = "sync.create.version.message";
    public static final String SYNC_AUDIT_MESSAGE_CONFLICT_KEY = "sync.create.version.conflict.message";
    public static final String SYNC_AUDIT_BEFORE_CONFLICT_KEY = "sync.create.version.conflict.before.message";
    
    
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }
    
    public void setFileFolderService(FileFolderService fileFolderService)
    {
        this.fileFolderService = fileFolderService;
    }
    
    public void setCheckOutCheckInService(CheckOutCheckInService service)
    {
        this.cociService = service;
    }
    
    public void setContentService(ContentService contentService)
    {
        this.contentService = contentService;
    }
    
    public void setBehaviourFilter(BehaviourFilter behaviourFilter)
    {
        this.behaviourFilter = behaviourFilter;
    }
    
    public void setSyncAdminService(SyncAdminService syncAdminService)
    {
        this.syncAdminService = syncAdminService;
    }
    
    public void setSyncChangeMonitor(SyncChangeMonitor monitor)
    {
        this.syncChangeMonitor = monitor;
    }
    
    public void setSyncAuditService(SyncAuditService syncAuditService)
    {
        this.syncAuditService = syncAuditService;
    }
    
    public void setSsmnChangeManagement(SsmnChangeManagement ssmnChangeManagement)
    {
        this.ssmnChangeManagement = ssmnChangeManagement;
    }
    
    public void setVersionService(VersionService versionService)
    {
        this.versionService = versionService;
    }
    
    public void setRetryingTransactionHelper(RetryingTransactionHelper retryingTransactionHelper)
    {
        this.retryingTransactionHelper = retryingTransactionHelper;
    }
    
    public void setLockService(LockService lockService)
    {
        this.lockService = lockService;
    }
    
    
    public void init()
    {
        PropertyCheck.mandatory(this, "nodeService", nodeService);
        PropertyCheck.mandatory(this, "fileFolderService", fileFolderService);
        PropertyCheck.mandatory(this, "contentService", contentService);
        PropertyCheck.mandatory(this, "behaviourFilter", behaviourFilter);
        PropertyCheck.mandatory(this, "syncAuditService", syncAuditService);
        PropertyCheck.mandatory(this, "syncAdminService", syncAdminService);
        PropertyCheck.mandatory(this, "syncChangeMonitor", syncChangeMonitor);
        PropertyCheck.mandatory(this, "ssmnChangeManagement", ssmnChangeManagement);
        PropertyCheck.mandatory(this, "versionService", versionService);
        PropertyCheck.mandatory(this, "lockService", lockService);
        // RemoteConnectorService is only mandatory On Premise.
    }
    
    @Override
    public SyncNodeChangesInfo fetchForPull(SyncNodeChangesInfo stub)
    {
        try
        {
            final NodeRef localNodeRef = stub.getLocalNodeRef();
            NodeRef remoteNodeRef = stub.getRemoteNodeRef();
            String ssdId = stub.getSyncSetGUID();
            
            ParameterCheck.mandatoryString("ssdId", ssdId);
            ParameterCheck.mandatory("localNodeRef", localNodeRef);
            
            if (logger.isDebugEnabled())
            {
                logger.debug("fetchForPull: " + localNodeRef);
            }
            
            /*
             *  MER - I think the code below is very suspicious - and certainly should not be throwing an 
             *  UNKNOWN exception. 
             */
            if( stub.getType() != null)
            {
            	if(stub.getType().equals(SyncModel.TYPE_SYNC_SET_DEFINITION))	
            	{
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("fetchForPull: clear audit (for SSD node): " +localNodeRef);
                    }
            		// cleanup (eg. for any previously recorded SSD_TO_DELETE events prior to ALF-15734 fix)
            		retryingTransactionHelper.doInTransaction(new RetryingTransactionCallback<Void>()
            		{
                        @Override
                        public Void execute() throws Throwable
                        {
                            clearSyncAudit(localNodeRef);
                            return null;
                        }
                    }, false, true);
                
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("fetchForPull: throw UNKNOWN exception)");
                    }
                
                    throw new SyncNodeException(SyncNodeExceptionType.UNKNOWN);
                }
            }
            
            // Fetch the changes for the node
            List<SyncChangeEvent> changes = syncAuditService.queryByNodeRef(localNodeRef, 10000);
            
            // Aggregate these into a single change
            AggregatedNodeChange change = ssmnChangeManagement.combine(changes);
            
            // Check it's something we can send back, then do if it's fine
            if ((change != null) && 
                ((change.getChangeType() == SsmnChangeType.CREATE) || 
                 (change.getChangeType() == SsmnChangeType.UPDATE) ||
                 (change.getChangeType() == SsmnChangeType.REMOVE) ||
                 (change.getChangeType() == SsmnChangeType.DELETE)))
            {
                SyncNodeChangesInfo changeInfo = change.getSyncNodeChangesInfo();
                ((SyncNodeChangesInfoImpl)changeInfo).setRemoteNodeRef(remoteNodeRef);
                return changeInfo;
                
            }
            else
            {
                throw new AlfrescoRuntimeException("Can't pull a change of type " + (change == null ? "null" : change.getChangeType()));
            }
        }
        catch (InvalidNodeRefException inre)
        {
            // belts-and-braces
            final NodeRef nodeRef = inre.getNodeRef();
            
            if (logger.isDebugEnabled())
            {
                logger.debug("fetchForPull: node does not exist (InvalidNodeRefException): "+nodeRef);
            }
            
            // note: could return an SSMN_REMOVED event and let the pull confirm
            retryingTransactionHelper.doInTransaction(new RetryingTransactionCallback<Void>()
            {
                @Override
                public Void execute() throws Throwable
                {
                    clearSyncAudit(nodeRef);
                    return null;
                }
            }, false, true);
            
            
            throw new SyncNodeException(SyncNodeExceptionType.TARGET_NODE_NO_LONGER_EXISTS);
        }
    }
    
    @Override
    public NodeRef create(SyncNodeChangesInfo newNode, boolean isOnCloud)
    {
        Long startTime = null;
        if (logger.isInfoEnabled())
        {
            startTime = System.currentTimeMillis();
        }
        
        NodeRef parentNodeRef = newNode.getLocalParentNodeRef();
        String ssdId = newNode.getSyncSetGUID();
        QName typeQName = newNode.getType();
        String versionLabel = newNode.getRemoteVersionLabel();
        Map<QName, Serializable> properties = newNode.getPropertyUpdates();
        
        ParameterCheck.mandatoryString("ssdId", ssdId);
        ParameterCheck.mandatory("parentNodeRef", parentNodeRef);
        ParameterCheck.mandatory("typeQName", typeQName);
        ParameterCheck.mandatory("propertyUpdates", properties);
        
        SyncSetDefinition ssd = syncAdminService.getSyncSetDefinition(ssdId);
        if (ssd == null)
        {
            throw new NoSuchSyncSetDefinitionException("No such Sync Set Definition", ssdId);
        }
        
        String name = (String)properties.get(ContentModel.PROP_NAME);
        ParameterCheck.mandatory("propertyUpdates.PROP_NAME", name);
        
        /**
         * Check whether the node has already been created for this sync set - could be a retry
         */
        NodeRef existingNode = fileFolderService.searchSimple(parentNodeRef, name);
        if(existingNode != null)
        {
            if(logger.isDebugEnabled())
            {
                logger.debug("node already exists with the specified name:" + name);
            }
            
            SyncSetDefinition existingSSD = syncAdminService.getSyncSetDefinition(existingNode);

            if(existingSSD != null)
            {
                if(existingSSD.getId().equals(ssdId))
                {
                    // this is a duplicate create - node is already in the same sync set
                    return existingNode;
                }
                throw new SyncNodeException(SyncNodeExceptionType.TARGET_NODE_ALREADY_SYNCED);
            }
            
        } 
        
        try
        {
            syncChangeMonitor.disableSyncBehaviours();
            
            FileInfo info; 
            try
            {
                info = fileFolderService.create(parentNodeRef, name, typeQName);
            }
            catch (InvalidNodeRefException ie)
            {
                throw new SyncNodeException(SyncNodeExceptionType.TARGET_FOLDER_NOT_FOUND);
            }
            catch (FileExistsException fe)
            {
                throw new SyncNodeException(SyncNodeExceptionType.TARGET_FOLDER_NAME_CLASH);
            }
            
            NodeRef nodeRef = info.getNodeRef();
            
            applyChanges(nodeRef, newNode);
            
            /**
             * Now we have created the new node with the relevant name, aspects and properties
             * wire up for sync service.
             */
            syncAdminService.addSyncSetMember(ssd, nodeRef);
            nodeService.setProperty(nodeRef, SyncModel.PROP_DIRECT_SYNC, newNode.getDirectSync());
            
            // Note that normally the call to addSyncSetMember(), which creates the assoc from SSD to memberNode, would
            // trigger a behaviour which copied down the syncSetOwner. However, we needed to disable sync behaviours here.
            // So we must do it manually.
            if (ssd.getSyncCreator() != null)
            {
                nodeService.setProperty(nodeRef, SyncModel.PROP_SYNC_OWNER, ssd.getSyncCreator());
            }
            
            
            /**
             * Need to wire up first version with the version service
             */ 
            Map<QName, Serializable> aspectProperties = new HashMap<QName, Serializable>(); 
            if(versionLabel != null)
            {
                if(logger.isDebugEnabled())
                {
                    logger.debug("set version label to versionLabel:" + versionLabel);
                }
                aspectProperties.put(ContentModel.PROP_VERSION_LABEL, versionLabel);
            }
            
            /**
             * Record the first successful sync
             */
            Map<QName, Serializable> syncProperties = new HashMap<QName, Serializable>();
            //syncProperties.put(SyncModel.PROP_SYNCED_THIS_VERSION_LABEL, retVal.getResolution());
            syncProperties.put(SyncModel.PROP_SYNCED_OTHER_VERSION_LABEL, newNode.getRemoteVersionLabel());
            nodeService.addAspect(nodeRef, SyncModel.ASPECT_SYNCED, syncProperties);
            
            nodeService.setProperty(nodeRef, SyncModel.PROP_SYNC_TIME, new Date());
            
            /**
             * Record the other node ref
             */
            if(newNode.getRemoteNodeRef() != null)
            {
                nodeService.setProperty(nodeRef, SyncModel.PROP_OTHER_NODEREF_STRING, newNode.getRemoteNodeRef().toString());
            }
            
            /*
             *  auto version will default to true - turn it off in anticipation of the 
             *  createVersion below 
             */
            nodeService.setProperty(nodeRef, ContentModel.PROP_AUTO_VERSION_PROPS, false);
            
            /**
             *  do we need to lock the new node?            
             */
            Boolean isLockOnPremise = ssd.getLockSourceCopy();
            if(isLockOnPremise)
            {
                if(logger.isDebugEnabled())
                {
                    logger.debug("applying sync lock to node:" + nodeRef);
                }
                // add a property to say that this was locked by sync - so we know we can unlock it after sync
                nodeService.setProperty(nodeRef, SyncModel.PROP_SYNC_LOCK, true);
                lockService.lock(nodeRef, LockType.NODE_LOCK);
                // allow the rest of the method to continue
                lockService.suspendLocks();
            }  
            
            Boolean isDeleteOnCloud = ssd.isDeleteOnCloud();
            
            /*
             * Do we need to prevent delete on cloud
             */
            if(!isDeleteOnCloud && isOnCloud)
            {
            	// Need to prevent deletes on cloud by adding undeletable
            	nodeService.addAspect(nodeRef, ContentModel.ASPECT_UNDELETABLE, null);
            	// behaviourFilter.disableBehaviour(nodeRef, ContentModel.ASPECT_UNDELETABLE);
            }
            
            /**
             * Do we need to set the auditable properties? - may as well on create
             */
            Date modifiedAt = newNode.getRemoteModifiedAt();
            if(modifiedAt != null)
            {
                if (logger.isTraceEnabled())
                {
                    logger.trace("setting modified date: "+nodeRef);
                }
                
                try
                {
                    behaviourFilter.disableBehaviour(nodeRef, ContentModel.ASPECT_AUDITABLE);
                    nodeService.setProperty(nodeRef, ContentModel.PROP_MODIFIED, modifiedAt);
                    nodeService.setProperty(nodeRef, SyncModel.PROP_REMOTE_MODIFIED, modifiedAt);
                }
                finally
                {
                    behaviourFilter.enableBehaviour(nodeRef, ContentModel.ASPECT_AUDITABLE);
                }
            }
            
            /**
             * Now the new node is created force a new version.
             */
            createVersion(nodeRef, true, VersionKey.NO_CONFLICT, newNode.getRemotePath());
            
            if (startTime != null)
            {
                ((SyncNodeChangesInfoImpl)newNode).setLocalNodeRef(nodeRef);
                
                StringBuilder sb = new StringBuilder("Sync: create - ");
                sb.append(newNode).append(" [in ").append(System.currentTimeMillis()-startTime).append(" ms]");
                logger.info(sb.toString());
            }
            
            return nodeRef;
        }
        catch (AccessDeniedException pe)
        {
            throw new SyncNodeException(SyncNodeExceptionType.TARGET_NODE_ACCESS_DENIED);
        }
        finally
        {
            syncChangeMonitor.enableSyncBehaviours();
        }
    }

    @Override
    public void delete(SyncNodeChangesInfo changes, boolean force)
    {
        Long startTime = null;
        if (logger.isInfoEnabled())
        {
            startTime = System.currentTimeMillis();
        }
        
        NodeRef nodeRef = changes.getLocalNodeRef();
        String ssdId = changes.getSyncSetGUID();
        
        ParameterCheck.mandatoryString("ssdId", ssdId);
        ParameterCheck.mandatory("nodeRef", nodeRef);
        
        /**
         * Make sure node still exists - if not do we care!
         */
        if(!nodeService.exists(nodeRef))
        {
            if(logger.isDebugEnabled())
            {
                logger.debug("Node did not exist, nothing to do:" + nodeRef);
            }
            return;
        }
        
        
        /**
         * does the SyncSet Definition actually exist 
         */        
        SyncSetDefinition ssd = syncAdminService.getSyncSetDefinition(nodeRef);
        if (ssd == null)
        {
            throw new NoSuchSyncSetDefinitionException("No such SSD", ssdId);
        }
        
        /**
         * Make sure existing node is in the right sync set
         */
        if(!ssd.getId().equalsIgnoreCase(ssdId))
        {
            throw new SyncNodeException(SyncNodeExceptionType.TARGET_NODE_OTHER_SYNC_SET);
        }
        
        /**
         * Check for conflicts
         */
        List<SyncChangeEvent> conflicts = syncAuditService.queryByNodeRef(nodeRef, 1);
        
        if(!force && conflicts.size() > 0)
        {  
            throw new ConcurrentModificationException("Unable to delete, node has conflicting updates + nodeRef:" + nodeRef);
        }
        
        clearSyncAudit(nodeRef);
        
        /*
         * Allow the sync process to delete a undeletable node
         */
        Boolean isDeleteOnCloud = ssd.isDeleteOnCloud();
        if(!isDeleteOnCloud)
        {
        	behaviourFilter.disableBehaviour(nodeRef, ContentModel.ASPECT_UNDELETABLE);
        }
        
       /**
         * Go ahead and delete the node
         */
        syncChangeMonitor.disableSyncBehaviours();
        
        try
        {
            syncAdminService.removeSyncSetMember(ssd, nodeRef);
            fileFolderService.delete(nodeRef);
        }
        catch (AccessDeniedException pe)
        {
            throw new SyncNodeException(SyncNodeExceptionType.TARGET_NODE_ACCESS_DENIED);
        }
        
        if (startTime != null)
        {
            StringBuilder sb = new StringBuilder("Sync: deleted - ");
            sb.append(changes).append(" [in ").append(System.currentTimeMillis()-startTime).append(" ms]");
            logger.info(sb.toString());
        }
    }
    
    @Override
    public void removeFromSyncSet(SyncNodeChangesInfo changes, boolean force)
    {
        Long startTime = null;
        if (logger.isInfoEnabled())
        {
            startTime = System.currentTimeMillis();
        }
        
        NodeRef nodeRef = changes.getLocalNodeRef();
        String ssdId = changes.getSyncSetGUID();
        
        ParameterCheck.mandatoryString("ssdId", ssdId);
        ParameterCheck.mandatory("nodeRef", nodeRef);
        
        SyncSetDefinition ssd = syncAdminService.getSyncSetDefinition(ssdId);
        if (ssd == null)
        {
            throw new NoSuchSyncSetDefinitionException("No such SSD", ssdId);
        }
        
        /**
         * Check for conflicts
         */
        List<SyncChangeEvent> conflicts = syncAuditService.queryByNodeRef(nodeRef, 2);
        if(!force && conflicts.size() > 0)
        {
            throw new ConcurrentModificationException("Unable to delete, node has conflicting updates + nodeRef:" + nodeRef);
        }
        
        /**
         * Delete any remaining audit history.   Did consider adding a higher performance method to 
         * audit service (DELETE WHERE).  However for now we don't expect much, if any conflict stuff.
         */
        while(conflicts.size() > 0)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("deleting conflicted audit entries size:" + conflicts.size());
            }
            long[] toDelete = new long[conflicts.size()];
            for( int i = 0; i < conflicts.size(); i++)
            {
                SyncChangeEvent event = conflicts.get(i);
                toDelete[i] = event.getAuditId();
            }
            syncAuditService.deleteAuditEntries(toDelete);
            conflicts = syncAuditService.queryByNodeRef(nodeRef, 100);
        }
        
        /**
         * Go ahead and remove this from the sync set
         */
        syncChangeMonitor.disableSyncBehaviours();
        
        syncAdminService.removeSyncSetMember(ssd, nodeRef);
        
        /*
         * Allow the sync process to delete a undeletable node
         */
        Boolean isDeleteOnCloud = ssd.isDeleteOnCloud();
        if(!isDeleteOnCloud && nodeService.hasAspect(nodeRef, ContentModel.ASPECT_UNDELETABLE))
        {
        	nodeService.removeAspect(nodeRef, ContentModel.ASPECT_UNDELETABLE);
        }

        
        if (startTime != null)
        {
            StringBuilder sb = new StringBuilder("Sync: removed from sync - ");
            sb.append(changes).append(" [in ").append(System.currentTimeMillis()-startTime).append(" ms]");
            logger.info(sb.toString());
        }
    }
    
    @Override
    public void forceUpdate(SyncNodeChangesInfo change)
            throws SyncNodeException
    {
        try
        {
            updateImpl(change, true);
        }
        catch (AccessDeniedException pe)
        {
            throw new SyncNodeException(SyncNodeExceptionType.TARGET_NODE_ACCESS_DENIED);
        }
    }
    
    @Override
    public void update(SyncNodeChangesInfo change)
            throws ConcurrentModificationException, SyncNodeException
    {
        try
        {
            Long startTime = null;
            if (logger.isInfoEnabled())
            {
                startTime = System.currentTimeMillis();
            }
            
            updateImpl(change, false);
            
            if (startTime != null)
            {
                StringBuilder sb = new StringBuilder("Sync: updated - ");
                sb.append(change).append(" [in ").append(System.currentTimeMillis()-startTime).append(" ms]");
                logger.info(sb.toString());
            }
        }
        catch (AccessDeniedException pe)
        {
            throw new SyncNodeException(SyncNodeExceptionType.TARGET_NODE_ACCESS_DENIED);
        }
    }
    
    private void addSyncNodeProps(NodeRef nodeRef, Map<QName, Serializable> syncProperties)
    {
        nodeService.addAspect(nodeRef, SyncModel.ASPECT_SYNCED, syncProperties);
        
        NodeRef wcNodeRef = cociService.getWorkingCopy(nodeRef);
        if (wcNodeRef != null)
        {
            // deal with working copy
            try
            {
                behaviourFilter.disableBehaviour(wcNodeRef, ContentModel.ASPECT_AUDITABLE);
                nodeService.addAspect(wcNodeRef, SyncModel.ASPECT_SYNCED, syncProperties);
            }
            finally
            {
                behaviourFilter.enableBehaviour(wcNodeRef, ContentModel.ASPECT_AUDITABLE);
            }
        }
    }
    
    private void updateImpl(SyncNodeChangesInfo change, boolean force)
            throws ConcurrentModificationException, SyncNodeException
    {
        String ssdId = change.getSyncSetGUID();
        NodeRef nodeRef = change.getLocalNodeRef();
        
        ParameterCheck.mandatoryString("ssdId", ssdId);
        ParameterCheck.mandatory("localNodeRef", nodeRef);
        
        if (logger.isDebugEnabled())
        {
            logger.debug("updateImpl: ssdId=" + ssdId + ", localNodeRef=" + nodeRef +", force=" + force);
        }
        
        /**
         * Make sure node still exists
         */
        if (! nodeService.exists(nodeRef))
        {
            if (logger.isInfoEnabled())
            {
                logger.info("updateImpl: target node no longer exists" + nodeRef);
            }
            throw new SyncNodeException(SyncNodeExceptionType.TARGET_NODE_NO_LONGER_EXISTS);
        }
        
        /**
         * Make sure sync set still exists
         */
        SyncSetDefinition ssd = syncAdminService.getSyncSetDefinition(ssdId);
        if (ssd == null)
        {
            if (logger.isInfoEnabled())
            {
                logger.info("updateImpl: no such SSD: ssdId=" + ssdId + ", localNodeRef=" + nodeRef);
            }
            throw new NoSuchSyncSetDefinitionException("No such SSD", ssdId);
        }
        
        SyncSetDefinition ssdNode = syncAdminService.getSyncSetDefinition(nodeRef);
        if (ssdNode == null)
        {
            // eg. due to force unsync
            // TODO is it really still in the way (or has it since been moved out of the way - ie. can we create a new sync rather than an update) ?
            if (logger.isInfoEnabled())
            {
                logger.info("updateImpl: target node exists but is not synced: ssdId=" + ssdId + ", localNodeRef=" + nodeRef);
            }
            throw new NodeNotSyncRelatedException("Node was not a sync member: " + nodeRef);
        }
        
        /**
         * Make sure existing node is in the right sync set
         */
        if (! ssdNode.getId().equalsIgnoreCase(ssdId))
        {
            if(logger.isInfoEnabled())
            {
                logger.info("updateImpl: update for a different sync set currentSyncSet:[" + ssdId + "] expectedSyncSet:[" +ssdNode.getId() + "]");
            }
            throw new SyncNodeException(SyncNodeExceptionType.TARGET_NODE_OTHER_SYNC_SET);
        }
        
        List<SyncChangeEvent> conflicts = syncAuditService.queryByNodeRef(nodeRef, 2);
        
        /**
         * Check whether update can go ahead, in particular
         * are there any conflicting changes? If there any unsynced changes, 
         * it's a conflict. throw ConcurrentModificationException. 
         */
        if (conflicts.size() > 0 && !force)
        {
            if(logger.isInfoEnabled())
            {
                logger.info("updateImpl: conflicting update: ssdId=" + ssdId + ", localNodeRef=" + nodeRef);
            }
            throw new ConcurrentModificationException("conflicting update: "+nodeRef);
        }
        
        if (lockService.getLockStatus(nodeRef) != LockStatus.NO_LOCK)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("updateImpl: node to update is locked - suspend lock service: "+nodeRef);
            }
            lockService.suspendLocks();
        }
        
        /**
         * Make sure the node is versioned correctly prior to the sync update
         * 
         * Two special cases
         * a) Where we need to lazy version for share
         * b) Where we are about to force a conflict
         */
        if(nodeService.hasAspect(nodeRef, ContentModel.ASPECT_VERSIONABLE))
        {
            if(force)
            {
                createVersion(nodeRef, false, VersionKey.BEFORE_CONFLICT, change.getRemotePath());
            }
        }
        else
        {
            /**
             * Make sure the node is versionable, this deals with lazy versioning 
             * by Share of existing content prior to the update by sync.
             */
            Map<QName, Serializable> versionProps = new HashMap<QName, Serializable>();
            versionProps.put(ContentModel.PROP_AUTO_VERSION, true);
            versionProps.put(ContentModel.PROP_AUTO_VERSION_PROPS, false);
            versionService.ensureVersioningEnabled(nodeRef, versionProps);
        }
        
        /**
         * Need to suppress the sync audit service
         */
        syncChangeMonitor.disableSyncBehaviours();
        
        /**
         * Apply the new version
         */
        applyChanges(nodeRef, change);
        
        Map<QName, Serializable> syncProperties = new HashMap<QName, Serializable>();
        //syncProperties.put(SyncModel.PROP_SYNCED_THIS_VERSION_LABEL, retVal.getResolution());
        syncProperties.put(SyncModel.PROP_SYNCED_OTHER_VERSION_LABEL, change.getRemoteVersionLabel());
        syncProperties.put(SyncModel.PROP_SYNC_TIME, new Date());
        
        addSyncNodeProps(nodeRef, syncProperties);
        
        /**
         * Make sure the node is versioned after update by sync
         * 
         * Here to avoid ALF-15075 - don't attempt to set modification time on conflict.       
         */
        if(force)
        {
            createVersion(nodeRef, false, VersionKey.AFTER_CONFLICT , change.getRemotePath());
        }
        else
        {
            Date modifiedAt = change.getRemoteModifiedAt();
            if(modifiedAt != null)
            {
                try
                {
                    behaviourFilter.disableBehaviour(nodeRef, ContentModel.ASPECT_AUDITABLE);
                    nodeService.setProperty(nodeRef, ContentModel.PROP_MODIFIED, modifiedAt);
                    nodeService.setProperty(nodeRef, SyncModel.PROP_REMOTE_MODIFIED, modifiedAt);
                }
                finally
                {
                    behaviourFilter.enableBehaviour(nodeRef, ContentModel.ASPECT_AUDITABLE);
                }
            }
            
            createVersion(nodeRef, false, VersionKey.NO_CONFLICT , change.getRemotePath());
        }
        
        // create version should be here after ALF-15075 is fixed
        
        if (logger.isDebugEnabled())
        {
            logger.debug("updateImpl: Updated node: "+nodeRef);
        }
   }

    @Override
    public ConflictResponse dealWithConflictInAppropriateManner(
            SyncNodeChangesInfo changes
            )
    {
        // Method - No-op
        return null;
    }
    
    @Override
    public void requestSync(List<NodeRef> memberNodeRefs)
    {
        int processedCnt = 0;
        
        for (NodeRef nodeRef : memberNodeRefs)
        {
        	int cnt = requestSyncImpl(nodeRef, true);
        	processedCnt = processedCnt + cnt;
        }
         
        if (logger.isDebugEnabled())
        {
            logger.debug("Requested sync for "+processedCnt+" nodes");
        }
    }
    
    private int requestSyncImpl(NodeRef nodeRef, boolean all)
    {
        int processedCnt = 0;
        
        if (lockService.getLockStatus(nodeRef) != LockStatus.NO_LOCK)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("node to requestSync is locked - suspend lock service: "+nodeRef);
            }
            lockService.suspendLocks();
        }
        
        if (syncAdminService.isSyncSetMemberNode(nodeRef))
        {
            nodeService.setProperty(nodeRef, SyncModel.PROP_SYNC_REQUESTED, true);
            
            boolean failed = false;
            boolean missingOtherNode = false;
            
            if (nodeService.hasAspect(nodeRef, SyncModel.ASPECT_SYNC_FAILED))
            {
                failed = true;
                nodeService.removeAspect(nodeRef, SyncModel.ASPECT_SYNC_FAILED);
                
                clearSyncAudit(nodeRef);
            }
            
            if ((String)nodeService.getProperty(nodeRef, SyncModel.PROP_OTHER_NODEREF_STRING) == null)
            {
                missingOtherNode = true;
                
                clearSyncAudit(nodeRef);
            }
            
            if (missingOtherNode || failed || all)
            {
                List<SyncChangeEvent> changes = syncAuditService.queryByNodeRef(nodeRef, 1);
                if (changes.size() == 0)
                {
                    if ((String)nodeService.getProperty(nodeRef, SyncModel.PROP_OTHER_NODEREF_STRING) == null)
                    {
                        syncAuditService.recordSsmnAdded(nodeRef);
                        
                        if (logger.isDebugEnabled())
                        {
                            logger.debug("Request sync - recorded create: "+nodeRef);
                        }
                    }
                    else
                    {
                        syncAuditService.recordSsmnUpdateAll(nodeRef);
                        
                        if (logger.isDebugEnabled())
                        {
                            logger.debug("Request sync - recorded update: "+nodeRef);
                        }
                    }
                }
                
                processedCnt++;
            }
            
            FileInfo fileInfo = fileFolderService.getFileInfo(nodeRef);
            if (fileInfo.isFolder())
            {
                List<FileInfo> children = fileFolderService.list(nodeRef);
                for (FileInfo child : children)
                {
                    // recurse down but only request sync for missing/failed nodes in this case (note: can be across sync sets !!)
                    int cnt = requestSyncImpl(child.getNodeRef(), false);
                    processedCnt = processedCnt + cnt;
                }
            }
        }
        else
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Skipping request sync for: "+nodeRef+" - not an SSMN");
            }
        }
        
        return processedCnt;
    }
    
    /*
     *  Apply changes to the node
     */
    private void applyChanges(NodeRef nodeRef, SyncNodeChangesInfo changes)
    {
        Set<QName>aspectsToAdd = changes.getAspectsAdded();
        if(aspectsToAdd != null)
        {
            for(QName aspect : aspectsToAdd)
            {
                if (logger.isTraceEnabled())
                {
                    logger.trace("add aspect: aspect=" + aspect + ", nodeRef="+nodeRef);
                }
                nodeService.addAspect(nodeRef, aspect, null);
            }
        }
        Set<QName>aspectsToRemove = changes.getAspectsRemoved();
        if (aspectsToRemove != null)
        {
            for(QName aspect : aspectsToRemove)
            {
                if (logger.isTraceEnabled())
                {
                    logger.trace("remove aspect: aspect=" + aspect + ", nodeRef="+nodeRef);
                }
                nodeService.removeAspect(nodeRef, aspect);
            }
        }
        
        Map<QName, CloudSyncContent> content = changes.getContentUpdates();
        if(content != null)
        {
           for(QName propName : content.keySet())
           {
               if (logger.isTraceEnabled())
               {
                   logger.trace("copying new content");
               }
               ContentReader reader = content.get(propName).openReader();
               ContentWriter writer = contentService.getWriter(nodeRef, propName, true);
               writer.setEncoding(reader.getEncoding());
               writer.setMimetype(reader.getMimetype());
               writer.setLocale(reader.getLocale());
               writer.putContent(reader);
           }
        }
        
        Map<QName, Serializable> props = changes.getPropertyUpdates();
        if (props != null)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("apply prop updates: props=" + props + ", nodeRef="+nodeRef);
            }
            
            for (QName propName : props.keySet())
            {
                Serializable s = props.get(propName);
                if(s == null)
                {
                    nodeService.removeProperty(nodeRef, propName);
                }
                else
                {
                    if (logger.isTraceEnabled())
                    {
                        logger.trace("set property propName=" + propName + ", value=" + s);
                    }
                    nodeService.setProperty(nodeRef, propName, s);
                }
            }
        }
        
        ChildAssociationRef currentPrimaryAssocRef = nodeService.getPrimaryParent(nodeRef);
        if((currentPrimaryAssocRef != null) && (changes.getLocalParentNodeRef() != null))
        {
            if (! currentPrimaryAssocRef.getParentRef().equals(changes.getLocalParentNodeRef()))
            {
                if (logger.isTraceEnabled())
                {
                    logger.trace("apply move 'within' folder sync: newParentNodeRef=" + changes.getLocalParentNodeRef() + ", nodeRef="+nodeRef);
                }
                
                // assume move "within" (TODO check ssd is the same - or push check to method above ...
                nodeService.moveNode(nodeRef, changes.getLocalParentNodeRef(), currentPrimaryAssocRef.getTypeQName(), currentPrimaryAssocRef.getQName());
            }
        }
    }
    
    
    /**
     * create a new version with a "synced" comment.
     * @param nodeRef
     * @param major
     */
    private enum VersionKey
    {
        AFTER_CONFLICT,
        BEFORE_CONFLICT,
        NO_CONFLICT
    }
    private void createVersion(NodeRef nodeRef, boolean major, VersionKey versionKey, String remotePath)
    {
        /**
         * Message "synced by {user} at {datetime}"
         */
        Date now = new Date();
        
        Locale locale = null;
        Serializable localeProperty = nodeService.getProperty(nodeRef, ContentModel.PROP_LOCALE);
        
        if (localeProperty != null)
        {
            locale = DefaultTypeConverter.INSTANCE.convert(Locale.class, localeProperty);
        }
        
        String user = AuthenticationUtil.getRunAsUser();
        
        Map<String, Serializable> versionProps2 = new HashMap<String, Serializable>();
        
        String key = "";
        switch(versionKey)
        {
        case AFTER_CONFLICT:
            key = SYNC_AUDIT_MESSAGE_CONFLICT_KEY;
            break;
        case NO_CONFLICT:
            key = SYNC_AUDIT_MESSAGE_KEY;
            break;
        case BEFORE_CONFLICT:
            key = SYNC_AUDIT_BEFORE_CONFLICT_KEY;
            break;
        }
        
        String message = null;
        
        if(locale != null)
        {
            DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, locale);
            message = I18NUtil.getMessage(key, locale, user, df.format(now), remotePath);
        }
        else
        {
            DateFormat df = DateFormat.getDateInstance(DateFormat.LONG);
            message = I18NUtil.getMessage(key, user, df.format(now));
        }
        
        if(message != null)
        {
            versionProps2.put(VersionModel.PROP_DESCRIPTION, message);
        }
        if(major)
        {
            versionProps2.put(VersionModel.PROP_VERSION_TYPE, VersionType.MAJOR);
        }
        else
        {
            versionProps2.put(VersionModel.PROP_VERSION_TYPE, VersionType.MINOR);
        }
        
        
        versionService.createVersion(nodeRef, versionProps2);
    }
    
    private void clearSyncAudit(NodeRef nodeRef)
    {
        try
        {
            syncChangeMonitor.disableSyncBehaviours();
            
            List<SyncChangeEvent> changes = syncAuditService.queryByNodeRef(nodeRef, 100);
            
            // Delete any remaining audit history.
            // Did consider adding a higher performance method to audit service (DELETE WHERE).
            
            int deletedCnt = 0;
            while(changes.size() > 0)
            {
                long[] toDelete = new long[changes.size()];
                for( int i = 0; i < changes.size(); i++)
                {
                    SyncChangeEvent event = changes.get(i);
                    toDelete[i] = event.getAuditId();
                }
                syncAuditService.deleteAuditEntries(toDelete);
                deletedCnt = deletedCnt + toDelete.length;
                
                changes = syncAuditService.queryByNodeRef(nodeRef, 100);
            }
            
            if (logger.isDebugEnabled())
            {
                logger.debug("Deleted "+deletedCnt+" audit entries for: "+nodeRef);
            }
        }
        finally
        {
            syncChangeMonitor.enableSyncBehaviours();
        }
    }
}
