/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.sync;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.alfresco.enterprise.repo.sync.SyncNodeException.SyncNodeExceptionType;
import org.alfresco.enterprise.repo.sync.audit.SyncAuditService;
import org.alfresco.enterprise.repo.sync.audit.SyncChangeEvent;
import org.alfresco.enterprise.repo.sync.audit.SyncEventHandler.AuditEventId;
import org.alfresco.enterprise.repo.sync.deltas.AggregatedNodeChange;
import org.alfresco.enterprise.repo.sync.deltas.AggregatedNodeChange.SsmnChangeType;
import org.alfresco.enterprise.repo.sync.deltas.SsmnChangeManagement;
import org.alfresco.enterprise.repo.sync.transport.AuditToken;
import org.alfresco.enterprise.repo.sync.transport.CloudSyncMemberNodeTransport;
import org.alfresco.enterprise.repo.sync.transport.CloudSyncSetDefinitionTransport;
import org.alfresco.enterprise.repo.sync.transport.SyncNodeChangesInfo;
import org.alfresco.enterprise.repo.sync.transport.impl.AuditTokenImpl;
import org.alfresco.enterprise.repo.sync.transport.impl.SyncNodeChangesInfoImpl;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.batch.BatchProcessWorkProvider;
import org.alfresco.repo.batch.BatchProcessor;
import org.alfresco.repo.batch.BatchProcessor.BatchProcessWorkerAdaptor;
import org.alfresco.repo.lock.JobLockService;
import org.alfresco.repo.lock.JobLockService.JobLockRefreshCallback;
import org.alfresco.repo.lock.LockAcquisitionException;
import org.alfresco.repo.mode.ServerMode;
import org.alfresco.repo.mode.ServerModeProvider;
import org.alfresco.repo.policy.BehaviourFilter;
import org.alfresco.repo.security.authentication.AuthenticationException;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.repo.tenant.TenantService;
import org.alfresco.repo.tenant.TenantUtil;
import org.alfresco.repo.tenant.TenantUtil.TenantRunAsWork;
import org.alfresco.repo.transaction.AlfrescoTransactionSupport;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.cmr.attributes.AttributeService;
import org.alfresco.service.cmr.coci.CheckOutCheckInService;
import org.alfresco.service.cmr.lock.LockService;
import org.alfresco.service.cmr.remoteconnector.RemoteConnectorServerException;
import org.alfresco.service.cmr.remoteticket.RemoteSystemUnavailableException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AccessStatus;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.PropertyCheck;
import org.alfresco.util.PropertyMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.surf.util.I18NUtil;

/**
 * The Sync Tracker Component provides the ability for the source instance to push and pull sync'd nodes.
 * <p>
 * The sync tracker controls concurrency of the push and pull jobs.
 * <p>
 * It is the 'controller' for the Sync feature.
 * 
 * @author mrogers, janv
 * @since 4.1
 */
public class SyncTrackerComponent
{
    private static final Log logger = LogFactory.getLog(SyncTrackerComponent.class);

    private CloudSyncMemberNodeTransport memberNodeTransport;
    private CloudSyncSetDefinitionTransport syncSetDefinitionTransport;

    private JobLockService jobLockService;
    private RetryingTransactionHelper retryingTransactionHelper;
    private SyncAuditService syncAuditService;
    private SyncAdminService syncAdminService;
    private SyncService syncService;
    private TransactionService transactionService;
    private SsmnChangeManagement ssmnChangeManagement;
    private NodeService nodeService;
    private CheckOutCheckInService cociService;
    private PersonService personService;
    private LockService lockService;
    private AttributeService attributeService;
    private PermissionService permissionService;
    private ServerModeProvider serverModeProvider;
    private BehaviourFilter behaviourFilter;
    
    /**
     * Enables and disables the sync tracker.
     */
    private enum RemoteStatus
    {
        /**
         *  Server is up
         */
        UP,
        
        /**
         * Server is down
         */
        DOWN,
        
        /**
         * We don't know whether the server is up or down
         */
        UNKNOWN
    }
    
    private boolean enabled = true;
    
    /**
     * Is the remote system available - used for suppressing lots of error messages.
     */
    private RemoteStatus remoteSystemStatus = RemoteStatus.UNKNOWN;
   
    
    private long LOCK_TIME_TO_LIVE=10000;
    private long LOCK_REFRESH_TIME=5000;
    private int DEFAULT_MAX_RESULTS_FOR_SYNC_CHANGES=1000;
    
    final int SSD_BATCH_SIZE = 1;
    
    private int PUSH_THREAD_CNT = 1;
    private int PUSH_THREAD_CNT_MAX = 30;
    
    private int PULL_THREAD_CNT = 1;
    private int PULL_THREAD_CNT_MAX = 30;
    
    public void setPushThreadCnt(int pushThreadCnt)
    {
        if ((pushThreadCnt > 0) && (pushThreadCnt <= PUSH_THREAD_CNT_MAX))
        {
            this.PUSH_THREAD_CNT = pushThreadCnt;
        }
    }
    
    public void setPullThreadCnt(int pullThreadCnt)
    {
        if ((pullThreadCnt > 0) && (pullThreadCnt <= PULL_THREAD_CNT_MAX))
        {
            this.PULL_THREAD_CNT = pullThreadCnt;
        }
    }
    
    public void init()
    {
        PropertyCheck.mandatory(this, "jobLockService", jobLockService);
        PropertyCheck.mandatory(this, "memberNodeTransport", memberNodeTransport);
        PropertyCheck.mandatory(this, "retryingTransactionHelper", getRetryingTransactionHelper());
        PropertyCheck.mandatory(this, "syncAuditService", syncAuditService);
        PropertyCheck.mandatory(this, "syncAdminService", syncAdminService);
        PropertyCheck.mandatory(this, "ssmnChangeManagement", getSsmnChangeManagement());
        PropertyCheck.mandatory(this, "nodeService",  getNodeService());
        PropertyCheck.mandatory(this, "syncSetDefinitionTransport", getCloudSyncSyncSetDefinitionTransport());
        PropertyCheck.mandatory(this, "syncService", getSyncService());
        PropertyCheck.mandatory(this, "transactionService", getTransactionService());
        PropertyCheck.mandatory(this, "personService", getPersonService());
        PropertyCheck.mandatory(this, "lockService", getLockService());
        PropertyCheck.mandatory(this, "attributeService", getAttributeService());
        PropertyCheck.mandatory(this, "permissionService", getPermissionService());
        PropertyCheck.mandatory(this, "behaviourFilter", behaviourFilter);
        
    }
    
    /**
     * The ordered list of sync sets to pull 
     */
    private Queue<String> syncSetsToPull = new ConcurrentLinkedQueue<String>();
    
    /**
     * The ordered list of sync sets to push
     */
    private Queue<String> syncSetsToPush = new ConcurrentLinkedQueue<String>();
    
    /**
     * Push changes from this repo to cloud
     */
    public void push()
    {
        // Bypass if the system is in read-only mode
        if (transactionService.isReadOnly())
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("PUSH: sync bypassed; the system is read-only.");
            }
            return;
        }
        
        if (serverModeProvider.getServerMode() != ServerMode.PRODUCTION)
        {
            logger.trace("PUSH: sync bypassed; not production mode");
            return;
        }
        
        if (!isEnabled())
        {
            logger.trace("PUSH: sync bypassed; tracker not enabled");
            return;
        }
        
        if (!syncAdminService.isOnPremise())
        {
            //TODO - would be better to not enable job in first place.
            logger.trace("PUSH: sync bypassed; not on premise");
            return;
        }
        
        if (!syncAdminService.isEnabled())
        {
            //TODO - would be better to not enable job in first place.
            logger.trace("PUSH: sync bypassed; sync not enabled");
            return;
        }
        
        if (logger.isTraceEnabled())
        {
            logger.trace("PUSH: starting");
        }
        
        try
        {
            // Lock the push
            QName lockQName = QName.createQName(SyncModel.SYNC_MODEL_1_0_URI, "pushJob");
            String lockToken = jobLockService.getLock(lockQName, LOCK_TIME_TO_LIVE, 0, 1);
            TrackerJobLockRefreshCallback callback = new TrackerJobLockRefreshCallback();
            
            jobLockService.refreshLock(lockToken, lockQName, LOCK_REFRESH_TIME, callback);
            
            try
            {
                if (logger.isTraceEnabled())
                {
                    logger.trace("PUSH: job lock held");
                }
                
                try
                {
                    AuthenticationUtil.runAsSystem(new RunAsWork<Void>()
                    {
                        public Void doWork() throws Exception
                        {
                            pushImpl();
                            return null;
                        }
                    });
                }
                catch (RemoteSystemUnavailableException re)
                {
                    if (testAndSetRemoteSystemUnavailable())
                    {
                        // This is where push comms failure is logged on the first time
                        logger.error("PUSH: unable to push:" + re.getMessage());
                    }
                }
            }
            finally
            {
                if (logger.isTraceEnabled())
                {
                    logger.trace("PUSH: job finished");
                }
                
                // Release the locks on the job and stop refreshing
                callback.isActive = false;
                jobLockService.releaseLock(lockToken, lockQName);
            }
        }
        catch (LockAcquisitionException e)
        {
            if (logger.isDebugEnabled())
            {
                // probably already running - or repo could be read only
                logger.debug("PUSH: unable to obtain job lock - probably already running");
            }
        }
    }
    
    // BatchProcessWorker that runs work as another user - note: borrowed from HomeFolderProviderSynchronizer
    private abstract class RunAsWorker extends BatchProcessWorkerAdaptor<String>
    {
        final String userName;
        final String tenantDomain;
        final String name;
        
        public RunAsWorker(String userName, String tenantDomain, String name)
        {
            this.userName = userName;
            this.tenantDomain = tenantDomain;
            this.name = name;
        }
        
        public void process(final String ssdId) throws Throwable
        {
            // note: runAs before runAsTenant (to avoid clearing tenant context, if no previous auth)
            AuthenticationUtil.runAs(new RunAsWork<Object>()
            {
                @Override
                public Object doWork() throws Exception
                {
                    return TenantUtil.runAsTenant(new TenantRunAsWork<Void>()
                    {
                        public Void doWork() throws Exception
                        {
                            RunAsWorker.this.doWork(ssdId);
                            return null;
                        }
                    }, tenantDomain);
                }
            }, userName);
        }
        
        public abstract void doWork(String ssdId) throws Exception;
        
        @SuppressWarnings("unused")
        public String getName()
        {
            return name;
        }
    };
    
    // TODO refactor
    private void setNodeSyncProps(NodeRef sourceNodeRef, NodeRef targetNodeRefInner)
    {
        Date now = new Date();

        PropertyMap properties = new PropertyMap(3);
        
        if (targetNodeRefInner != null)
        {
            properties.put(SyncModel.PROP_OTHER_NODEREF_STRING, targetNodeRefInner);
        }
        
        properties.put(SyncModel.PROP_SYNC_TIME, now);
        properties.put(SyncModel.PROP_SYNC_REQUESTED, false);

        try
        {
            behaviourFilter.disableBehaviour(sourceNodeRef, ContentModel.ASPECT_AUDITABLE);
            nodeService.addProperties(sourceNodeRef, properties);
        }
        finally
        {
            behaviourFilter.enableBehaviour(sourceNodeRef, ContentModel.ASPECT_AUDITABLE);
        }
                
        NodeRef wcNodeRef = cociService.getWorkingCopy(sourceNodeRef);
        if (wcNodeRef != null)
        {
            // ALF-15130
            if (targetNodeRefInner != null)
            {
                nodeService.setProperty(wcNodeRef, SyncModel.PROP_OTHER_NODEREF_STRING, targetNodeRefInner);
            }
            nodeService.setProperty(wcNodeRef, SyncModel.PROP_SYNC_TIME, now);
            nodeService.setProperty(wcNodeRef, SyncModel.PROP_SYNC_REQUESTED, false);
        }
    }
    
    /**
     * Implementation of push - after all the locks are held.
     * 
     * This method also contains the transaction boundaries
     */
    private void pushImpl()
    {
        //
        // Transaction 1: obtain list of (changed) sync sets to push and put them on the queue
        //
        retryingTransactionHelper.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            @Override public Void execute() throws Throwable
            {
                String srcRepoId = syncAuditService.getRepoId();
                
                syncSetsToPush.clear();
                
                // Retrieve those local/source SSDs which contain unsynced changes (for given sourceRepoId)
                List<String> changedSyncSets = syncAuditService.querySsdManifest(srcRepoId, DEFAULT_MAX_RESULTS_FOR_SYNC_CHANGES);
                
                if (logger.isTraceEnabled()) 
                {
                   logger.trace("PUSH: retrieved " + changedSyncSets.size() + " sync sets for srcRepoId: "+srcRepoId+" ["+changedSyncSets+"]");
                }
                else if (logger.isDebugEnabled() && (changedSyncSets.size() > 0))
                { 
                    logger.debug("PUSH: retrieved " + changedSyncSets.size() + " sync sets for srcRepoId: "+srcRepoId);
                }
                
                for (String ssdId : changedSyncSets)
                {
                    if (!syncSetsToPush.contains(ssdId))
                    {
                        syncSetsToPush.add(ssdId);
                    }
                }
                return null;
            }
        }, true, true);
        
        if (syncSetsToPush.isEmpty())
        {
            // short-circuit - nothing to do
            return;
        }
        
        final String systemUserName = AuthenticationUtil.getSystemUserName();
        final String tenantDomain = TenantService.DEFAULT_DOMAIN;
        
        RunAsWorker worker = new RunAsWorker(systemUserName, tenantDomain, "pushSyncSet")
        {
            @Override
            public void doWork(String ssdId) throws Exception
            {
                try
                {
                    pushSyncSet(ssdId);
                }
                catch (AuthenticationException ae)
                {
                    // The sync set authentication is no good.
                    // The other sync sets may work though.
                    logger.error("PUSH: unable to authenticate to push sync set - "+ssdId, ae);
                    //TODO - prevent hundreds of exception messages
                }
            }
        };
        
        final Iterator<String> ssdItr = syncSetsToPush.iterator();
        
        BatchProcessWorkProvider<String> provider = new BatchProcessWorkProvider<String>()
        {
            @Override
            public int getTotalEstimatedWorkSize()
            {
                return syncSetsToPush.size();
            }
            
            @Override
            public Collection<String> getNextWork()
            {
                int batchCount = 0;
                
                List<String> siteBatch = new ArrayList<String>(SSD_BATCH_SIZE);
                while (ssdItr.hasNext() && (batchCount++ != SSD_BATCH_SIZE))
                {
                    siteBatch.add(ssdItr.next());
                }
                return siteBatch;
            }
        };
        
        final RetryingTransactionHelper txHelper = transactionService.getRetryingTransactionHelper();
        txHelper.setMaxRetries(0);
        
        BatchProcessor<String> processor = new BatchProcessor<String>(
                "SyncTrackerPush",
                txHelper,
                provider,
                PUSH_THREAD_CNT, SSD_BATCH_SIZE,
                null,
                logger, 100);
        
        // go parallel here - process each sync set
        processor.process(worker, true);
        
        if (processor.getTotalErrors() > 0)
        {
            logger.info("PUSH: batch of syncSets processed with "+processor.getTotalErrors()+" errors");
        }
    }
    
    /**
     * Push a sync set
     * @param syncSetId
     * @throws AuthenticationException
     * @throws RemoteSystemUnavailableException
     */
    private void pushSyncSet(final String syncSetId)
    {
        long startTime = System.currentTimeMillis();
        
        if (logger.isDebugEnabled())
        {
            logger.debug("PUSH: start syncSetId '"+syncSetId+"' ["+AlfrescoTransactionSupport.getTransactionId()+"]");
        }
        
        //
        // Transaction 2: for this sync set - get the sync creator
        //
        final String syncCreatorSourceUsername = retryingTransactionHelper.doInTransaction(new RetryingTransactionCallback<String>()
        {
            @Override
            public String execute() throws Throwable
            {
                String syncCreatorSourceUsername = null;
                final SyncSetDefinition ssd = syncAdminService.getSyncSetDefinition(syncSetId);
                
                if (ssd == null)
                {
                    // Audit entries exist for a sync set which doesn't exist.          
                    // Delete in chunks of 200 - may take a few iterations to drain the audit - but it will clean
                    List <SyncChangeEvent> events = syncAuditService.queryBySsdId(syncSetId, 200);
                    
                    final long auditEntryIds[] = new long[events.size()];
                    int i = 0;
                    for (SyncChangeEvent event : events)
                    {
                        auditEntryIds[i++]=event.getAuditId();
                    }
                    
                    deleteAuditEntriesImpl(auditEntryIds);
                    
                    if (logger.isWarnEnabled())
                    {
                        logger.warn("PUSH: Cannot push sync set changes ("+syncSetId+") - sdd not found: "+ssd+" - cleared "+auditEntryIds.length+" audit entries");
                    }
                }
                else
                {
                    // SSD is available
                    syncCreatorSourceUsername = ssd.getSyncCreator();
                    if (syncCreatorSourceUsername == null)
                    {
                        if (logger.isWarnEnabled())
                        {
                            logger.warn("PUSH: Cannot push sync set changes ("+syncSetId+") - creator username: '" + syncCreatorSourceUsername + "'");
                        }
                    }
                    else if (! personService.personExists(syncCreatorSourceUsername))
                    {
                        /*
                         *  Here with audit entries for a sync set for which the owner does not exist.          
                         */
                        if (logger.isWarnEnabled())
                        {
                            logger.warn("PUSH: Cannot push sync set changes ("+syncSetId+") - creator username person does not exist: '" + syncCreatorSourceUsername + "'");
                        }
                                              
                        AuthenticationUtil.pushAuthentication();
                        try
                        {
                            AuthenticationUtil.setRunAsUserSystem();
                           
                            //  Delete audit in chunks of 1000 - may take a few iterations to drain the audit - but it will clean
                            final List <SyncChangeEvent> events = syncAuditService.queryBySsdId(syncSetId, 1000);
                            
                            // Special case where we want to delete the ssd but the owner is not available
                            for(SyncChangeEvent event : events)
                            {
                                if (event.getEventId().equals(AuditEventId.SSD_TO_DELETE))
                                {
                                    // yes we got a SSD_TO_DELETE event
                                    syncCreatorSourceUsername = event.getUser();
                                    
                                    if(syncCreatorSourceUsername != null)
                                    {
                                        if(logger.isDebugEnabled())
                                        {
                                            logger.debug("special case, sync set owner does not exist - substituting user:" + syncCreatorSourceUsername);
                                        }
                                        return syncCreatorSourceUsername;
                                    }
                                }
                            }
                        
                            /**
                             * Mark nodes with failed aspect.
                             */
                            for(SyncChangeEvent event : events)
                            {
                                NodeRef nodeRef = event.getNodeRef();
                            
                                if(nodeService.exists(nodeRef))
                                {
                                    if(!nodeService.hasAspect(nodeRef, SyncModel.ASPECT_SYNC_FAILED))
                                    {
                                        getLockService().suspendLocks();
                                        String details = I18NUtil.getMessage("sync.node.owner_not_found.description", syncCreatorSourceUsername);

                                        Map<QName, Serializable> aspectProperties = new HashMap<QName, Serializable>();
                                        aspectProperties.put(SyncModel.PROP_SYNCED_FAILED_CODE, SyncNodeException.SyncNodeExceptionType.OWNER_NOT_FOUND.getMessageId());
                                        aspectProperties.put(SyncModel.PROP_SYNCED_FAILED_DETAILS, details);
                                        aspectProperties.put(SyncModel.PROP_SYNCED_FAILED_TIME, new Date()); 
                                        nodeService.addAspect(nodeRef, SyncModel.ASPECT_SYNC_FAILED, aspectProperties);
                                    }
                                }
                            }
                        
                            final long auditEntryIds[] = new long[events.size()];
                            int i = 0;
                            for (SyncChangeEvent event : events)
                            {
                                auditEntryIds[i++]=event.getAuditId();
                            }
                        
                            deleteAuditEntriesImpl(auditEntryIds);
                        }
                        finally
                        {
                            AuthenticationUtil.popAuthentication();
                        }
                        
                        syncCreatorSourceUsername = null;
                        
                        syncCreatorSourceUsername = null;
                    }
                } // end if person does not exist
                
                return syncCreatorSourceUsername;
                
            }
        }, false, true);
        
        if (syncCreatorSourceUsername == null)
        {
            // nothing more to do here
            return;
        }
        
        AuthenticationUtil.pushAuthentication();
        try
        {
            // note: runAs block would set auditable property to be System
            AuthenticationUtil.setFullyAuthenticatedUser(syncCreatorSourceUsername);
            
            //
            // Transaction 3: for this sync set - get the changes
            //
            final List<SyncChangeEvent> events = retryingTransactionHelper.doInTransaction(new RetryingTransactionCallback<List<SyncChangeEvent>>()
            {
                @Override
                public List<SyncChangeEvent> execute() throws Throwable
                {
                    List<SyncChangeEvent> events = syncAuditService.queryBySsdId(syncSetId, 10000);
                    
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("PUSH: syncSetId '"+syncSetId+"' has "+events.size()+" events ["+AlfrescoTransactionSupport.getTransactionId()+"]");
                    }
                    
                    return events;
                }
            }, true, true);
            
            if (logger.isTraceEnabled())
            {
                logger.trace("PUSH: syncSetId: "+syncSetId+" - "+events);
            }
            
            // Restructure the List<SyncChangeEvent> into one per SSMN noderef - however we need to preserve order (ie. earliest for a given nodeRef)
            final int initialSize = events.size()/10;
            Map<NodeRef, List<SyncChangeEvent>> eventsByNode = new HashMap<NodeRef, List<SyncChangeEvent>>(initialSize);
            List<NodeRef> nodesInEventOrder = new ArrayList<NodeRef>(initialSize);
            
            for (final SyncChangeEvent event : events)
            {
                if (event.getEventId().equals(AuditEventId.SSD_TO_DELETE))
                {
                    //
                    // Transaction 4: for this sync set - delete sync set
                    //
                    retryingTransactionHelper.doInTransaction(new RetryingTransactionCallback<Void>()
                    {
                        @Override
                        public Void execute() throws Throwable
                        {
                            SyncSetDefinition ssd = syncAdminService.getSyncSetDefinition(event.getSsdId());
                            if (ssd == null)
                            {
                                if (logger.isWarnEnabled())
                                {
                                    logger.warn("PUSH: SSD TO DELETE event ("+event.getSsdId()+") but ssd does not exist or has already been deleted ! - event ignored+removed");
                                }
                            }
                            else
                            {
                                if ((syncAdminService.getMemberNodes(ssd).size() == 0) && (events.size() == 1))
                                {
                                    // only delete if there are no more SSMNs *and* no more events (this allows SSMN_DELETEs to be pushed through in the delete case)
                                    syncAdminService.deleteSourceSyncSet(ssd.getId());
                                    
                                    if (logger.isWarnEnabled())
                                    {
                                        logger.warn("PUSH: SSD TO DELETE event ("+event.getSsdId()+") - deleted source and target SSD");
                                    }
                                }
                                else
                                {
                                    if (logger.isWarnEnabled())
                                    {
                                        logger.warn("PUSH: SSD TO DELETE event ("+event.getSsdId()+") but ssd still has members ! - event ignored+removed");
                                    }
                                }
                            }
                            
                            // clear local/source audit entry
                            long[] auditEntryIds = new long[1];
                            auditEntryIds[0] = event.getAuditId();
                            deleteAuditEntriesImpl(auditEntryIds);
                            
                            return null;
                        }
                    }, false, true);
                    
                    continue;
                }
                
                NodeRef ssmn = event.getNodeRef();
                if (! nodesInEventOrder.contains(ssmn))
                {
                    nodesInEventOrder.add(ssmn);
                }
                
                List<SyncChangeEvent> eventsForThisNode = eventsByNode.get(ssmn);
                if (eventsForThisNode == null)
                {
                    eventsForThisNode = new ArrayList<SyncChangeEvent>();
                    eventsByNode.put(ssmn, eventsForThisNode);
                }
                eventsForThisNode.add(event);
            }
            
            int pushCnt = 0;
            // preserve node order (in terms of earliest event for each node)
            for (NodeRef ssmnNode : nodesInEventOrder)
            {
                if ((nodeService.exists(ssmnNode) && !nodeService.hasAspect(ssmnNode, ContentModel.ASPECT_HIDDEN)) || !nodeService.exists(ssmnNode))
                {
                    boolean success = pushNode(ssmnNode, eventsByNode.get(ssmnNode));
                    if (success)
                    {
                        pushCnt++;
                    }
                }
            }
            
            if (logger.isInfoEnabled())
            {
                int pushFailCnt = nodesInEventOrder.size()-pushCnt;
                logger.info("PUSH: pushed syncSet '"+syncSetId+"' nodes ("+pushCnt+" succeeded, "+pushFailCnt+" failed) [in "+(System.currentTimeMillis()-startTime)+" ms]");
            }
        }
        catch (RemoteConnectorServerException re)
        {
            // Something has gone wrong on the back end - may succeed for the next sync set though
            logger.error("Unable to push changes to target - syncSetId: "+ syncSetId, re);
        }
        finally
        {
            AuthenticationUtil.popAuthentication();
        }
    } // end of push sync set
    
    /**
     * Push a single node
     * 
     * @param sourceNodeRef the node to push
     * @param changeEventsForThisNode the changes for this node
     * 
     * @throws AuthenticationException
     * @throws RemoteSystemUnavailableException
     */
    private boolean pushNode(final NodeRef sourceNodeRef, final List<SyncChangeEvent> changeEventsForThisNode )
    {
        boolean result = false;
        
        final long startTime = System.currentTimeMillis();
        
        if (logger.isTraceEnabled())
        {
            logger.trace("PUSH: sourceNodeRef: "+sourceNodeRef+" ["+changeEventsForThisNode.size()+" change events]");
        }
        
        /**
         * Prepare audit id's for delete method
         */
        final long auditEntryIds[] = new long[changeEventsForThisNode.size()];
        int i = 0;
        for(SyncChangeEvent event : changeEventsForThisNode)
        {
            auditEntryIds[i++]=event.getAuditId();
        }
        
        final String syncSetId = changeEventsForThisNode.get(0).getSsdId();
        final SyncSetDefinition ssd = getSyncAdminService().getSyncSetDefinition(syncSetId);
        
         //TODO - Still need to push delete events - this code is in the wrong place.
        if (ssd == null)
        {
            if (logger.isWarnEnabled())
            {
                logger.warn("PUSH: Cannot push sourceNodeRef ("+sourceNodeRef+") - sdd not found: "+ssd);
            }
            
            deleteAuditEntriesImpl(auditEntryIds);
            
            // TODO cleanup target (if possible ?)
            return result;
        }
        
        final boolean sourceNodeRefExists = nodeService.exists(sourceNodeRef);
        
        if (sourceNodeRefExists && nodeService.hasAspect(sourceNodeRef, SyncModel.ASPECT_SYNC_FAILED))
        {
            if(logger.isDebugEnabled())
            {
                logger.debug("PUST: cannot push sourceNodeRef ("+sourceNodeRef+") - node already has failed aspect - drain audit");
            }
            // sync has failed for this node - we don't attempt to do anything more with it.
            deleteAuditEntriesImpl(auditEntryIds);
            return result;
        }
        
        final String cloudNetwork = ssd.getRemoteTenantId();
        
        NodeRef targetNodeRef = null;
        
        try
        {
            //
            // Transaction 5: primary node update transaction here
            //
            retryingTransactionHelper.doInTransaction(new RetryingTransactionCallback<Void>()
            {
                @Override
                public Void execute() throws Throwable
                {
                    final AggregatedNodeChange aggregatedChanges = getSsmnChangeManagement().combine(changeEventsForThisNode);
                    
                    // CloudSyncTransportService has different calls for create, delete & update
                    final SsmnChangeType ssmnChangeType = aggregatedChanges.getChangeType();
                    
                    if (ssmnChangeType == null)
                    {
                        return null;
                    }
                    
                    if ((! sourceNodeRefExists) && (! ssmnChangeType.equals(SsmnChangeType.DELETE)))
                    {
                        if (logger.isWarnEnabled())
                        {
                            logger.warn("PUSH: sourceNodeRef does not exist ('"+sourceNodeRef+"') and this is not a DELETE change ('"+ssmnChangeType+")");
                        }
                    }
                    
                    /**
                     * Allow the sync set member node properties to be updated even though the source node may be locked
                     */
                    getLockService().suspendLocks();
                    
                    NodeRef targetNodeRefInner = null;
                    
                    switch (ssmnChangeType)
                    { 
                        case CREATE:
                            if (permissionService.hasPermission(sourceNodeRef, PermissionService.WRITE) != AccessStatus.ALLOWED)
                            {
                                throw new SyncNodeException(SyncNodeExceptionType.SOURCE_NODE_ACCESS_DENIED);
                            }
                            
                            if (nodeService.hasAspect(sourceNodeRef, SyncModel.ASPECT_SYNC_SET_MEMBER_NODE))
                            {
                                targetNodeRefInner = memberNodeTransport.pushSyncInitial(aggregatedChanges.getSyncNodeChangesInfo(), cloudNetwork);
                                setNodeSyncProps(sourceNodeRef, targetNodeRefInner);
                            }
                            
                            break;
                           
                        case UPDATE:
                            if (permissionService.hasPermission(sourceNodeRef, PermissionService.WRITE) != AccessStatus.ALLOWED)
                            {
                                throw new SyncNodeException(SyncNodeExceptionType.SOURCE_NODE_ACCESS_DENIED);
                            }
                            memberNodeTransport.pushSyncChange(aggregatedChanges.getSyncNodeChangesInfo(), cloudNetwork);
                            
                            setNodeSyncProps(sourceNodeRef, null);
                            
                            targetNodeRefInner = aggregatedChanges.getSyncNodeChangesInfo().getRemoteNodeRef();
                            
                            break;
                            
                        case REMOVE:
                            targetNodeRefInner = aggregatedChanges.getSyncNodeChangesInfo().getRemoteNodeRef();
                            if (targetNodeRefInner != null)
                            {
                                memberNodeTransport.pushUnSync(aggregatedChanges.getSyncNodeChangesInfo(), cloudNetwork);
                            }
                            break;
                            
                        case DELETE:
                            targetNodeRefInner = aggregatedChanges.getSyncNodeChangesInfo().getRemoteNodeRef();
                            if (targetNodeRefInner != null)
                            {
                                memberNodeTransport.pushSyncDelete(aggregatedChanges.getSyncNodeChangesInfo(), cloudNetwork);
                            }
                            break;
                            
                        default:
                            throw new AlfrescoRuntimeException("PUSH: Unhandled " + SsmnChangeType.class.getSimpleName());
                    }
                    
                    if (logger.isInfoEnabled())
                    {
                        StringBuilder sb = new StringBuilder("PUSH: node pushed (changeType=").append(ssmnChangeType).
                           append(", sourceNodeRef=").append(sourceNodeRef).
                           append(", targetNodeRef=").append(targetNodeRefInner).
                           append(") [in ").append(System.currentTimeMillis()-startTime).append(" ms]");
                        
                        if (logger.isDebugEnabled())
                        {
                            sb.append(" [").append(AlfrescoTransactionSupport.getTransactionId()).append("]");
                        }
                        
                        logger.info(sb.toString());
                    }
                    
                    return null;
                }
            }, false, true);
            
            /**
             * If we get this far in the method we have had some sort normal flow
             *
             * Clean up the audit log.
             * 
             * In a conflict situation leave it up to the pull job to deal with.
             * 
             * The pull job may be able to resolve minor conflicts.
             */
            deleteAuditEntriesImpl(auditEntryIds);
            
            result = true;
        }
        catch (SyncNodeException se)
        {
           if(logger.isDebugEnabled())
           {
              logger.debug("got a sync node exception", se);
           }
           handleSyncNodeException(se, sourceNodeRef, auditEntryIds);
        }
        catch (RemoteSystemUnavailableException rsue)
        {
            // This one is transient, however it is handled higher up
            throw rsue;
        }
        catch (RemoteConnectorServerException rcse)
        {
            // These are HTTP Status 500s from the remote server
            if(logger.isDebugEnabled())
            {
                logger.debug("got a remote connector server exception", rcse);
            }
            logger.error("PUSH: unexpected remote connector server exception : push failed (sourceNodeRef="+sourceNodeRef+", targetNodeRef="+targetNodeRef, rcse);
            handleSyncNodeException(SyncNodeException.wrapUnhandledException(rcse), sourceNodeRef, auditEntryIds);
        }
        catch (AuthenticationException ae)
        {
            // Temporary solution - treat the authentication error as a "hard" error on the node. 
            handleSyncNodeException(new SyncNodeException(SyncNodeExceptionType.AUTHENTICATION_ERROR), sourceNodeRef, auditEntryIds);
            
            // TODO consider that this should be handled differently since this is not an 
            // error on a node but with the sync set owner's credentials.
            // throw ae;
        }
        catch (ConcurrentModificationException ce)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("PUSH: conflict for sourceNodeRef:" + sourceNodeRef);
            }
            /*
             * CONFLICTS ARE HANDLED BY PULL - local audit entries are left on the queue.
             */
        }
        catch(AlfrescoRuntimeException are)
        {
            // Temporary Work around for transport methods incorrectly wrapping exceptions
            Throwable t = are.getCause();
            if(t != null)
            {
                if (t instanceof IOException)
                {
                    throw new RemoteSystemUnavailableException("dummy");
                }
            }
            
            // Now need to do the equivalent of below
            // TODO we need to show error message user - not a generic UNKNOWN (see ALF-14872)
            // THIS CODE SHOULD NEVER HAPPEN AND INDICATES A PROBLEM            
            logger.error("PUSH: unexpected throwable : push failed (sourceNodeRef="+sourceNodeRef+", targetNodeRef="+targetNodeRef, are);
            handleSyncNodeException(SyncNodeException.wrapUnhandledException(are), sourceNodeRef, auditEntryIds);
        }
        catch (Throwable t)
        {
            
            // TODO we need to show error message user - not a generic UNKNOWN (see ALF-14872)
            // THIS CODE SHOULD NEVER HAPPEN AND INDICATES A PROBLEM
            logger.error("PUSH: unexpected throwable : push failed (sourceNodeRef="+sourceNodeRef+", targetNodeRef="+targetNodeRef, t);
            handleSyncNodeException(SyncNodeException.wrapUnhandledException(t), sourceNodeRef, auditEntryIds);
        }
        
        setRemoteSystemAvailable();
        
        return result;
    }
    
    private void handleSyncNodeException(final SyncNodeException se, final NodeRef sourceNodeRef, final long[] auditEntryIds)
    {
       /**
        * This exception means that comms are fine but the sync cannot work.
        */
       if (logger.isWarnEnabled())
       {
            logger.warn("PUSH: Unable to push node target - sourceNodeRef: "+sourceNodeRef, se);
       }
       
       // Needs to run as system so we don't have any permissions errors
       AuthenticationUtil.pushAuthentication();
       try
       {
           AuthenticationUtil.setRunAsUserSystem();
           
           retryingTransactionHelper.doInTransaction(new RetryingTransactionCallback<Void>()
           {
               @Override
               public Void execute() throws Throwable
               {
                   if (nodeService.exists(sourceNodeRef))
                   {
                       // Allow the sync failed properties to be written even if the node is locked.
                       getLockService().suspendLocks();

                       String details = "Unable to sync node " + se.getLocalizedMessage();
                       switch (se.getExceptionType())
                       {
                       case TARGET_NODE_NO_LONGER_EXISTS:
                           nodeService.removeProperty(sourceNodeRef,  SyncModel.PROP_OTHER_NODEREF_STRING);
                           details = I18NUtil.getMessage("sync.node.no_longer_exists.description");
                           break;

                       case UNKNOWN: 
                           String cause = "unknown";
                           if(se.getCause()!= null)
                           {
                               cause = se.getCause().getMessage();
                           }
                           details = I18NUtil.getMessage("sync.node.unknown.description", cause);
                           if (details == null)
                           {
                               details = cause;
                           }
                           break;
                           
                       case TARGET_NODE_OTHER_SYNC_SET:
                       case TARGET_FOLDER_NOT_FOUND:
                       case TARGET_NODE_ALREADY_SYNCED:
                       case TARGET_FOLDER_NAME_CLASH:
                       case TARGET_NODE_ACCESS_DENIED:
                       case QUOTA_LIMIT_VIOLATION:
                       case CONTENT_LIMIT_VIOLATION:
                           break;
                      default:
                          break;
                       }
                   
                       if (syncAdminService.isSyncSetMemberNode(sourceNodeRef))
                       {
                           nodeService.setProperty(sourceNodeRef, SyncModel.PROP_SYNC_REQUESTED, false);
                       
                           Map<QName, Serializable> props = new HashMap<QName, Serializable>(2);
                           props.put(SyncModel.PROP_SYNCED_FAILED_CODE, se.getMsgId());
                           props.put(SyncModel.PROP_SYNCED_FAILED_DETAILS, details);
                           props.put(SyncModel.PROP_SYNCED_FAILED_TIME, new Date());   
                           nodeService.addAspect(sourceNodeRef, SyncModel.ASPECT_SYNC_FAILED, props);
                       
                           if (logger.isDebugEnabled())
                           {
                               logger.debug("PUSH: Node marked as failed - sourceNodeRef: "+sourceNodeRef, se);
                           }
                       }
                       else
                       {
                           // eg. push of an unsync failed (note: it will not be possible to re-request - ok, if the other end no longer exists)
                           if (logger.isDebugEnabled())
                           {
                               logger.debug("PUSH: Node is no longer an SSMN - sourceNodeRef: "+sourceNodeRef, se);
                           }
                       }
                   }
                   else
                   {
                       if (logger.isInfoEnabled())
                       {
                           logger.info("PUSH: Unable to push node (source no longer exists) - sourceNodeRef: "+sourceNodeRef, se);
                       }
                   }
               
                   deleteAuditEntriesImpl(auditEntryIds);
                   return null;
               }
           }, false, true);
       } 
       finally
       {
           AuthenticationUtil.popAuthentication();
       }
    }
    
    /**
     * Delete audit entries in a separate transaction.
     * @param auditEntryIds audit entries to delete
     */
    private void deleteAuditEntriesImpl(final long[] auditEntryIds)
    {
        retryingTransactionHelper.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            @Override
            public Void execute() throws Throwable
            {
                syncAuditService.deleteAuditEntries(auditEntryIds);
                return null;
            }
        }, false, true);
    }
    
    
    /**
     * Pull changes to this repo from cloud
     */
    public void pull()
    {
        boolean preRequirementsOk = retryingTransactionHelper.doInTransaction(new RetryingTransactionCallback<Boolean>()
        {
            @Override
            public Boolean execute() throws Throwable
            {
                // Bypass if the system is in read-only mode
                if (transactionService.isReadOnly())
                {
                    if (logger.isTraceEnabled())
                    {
                        logger.trace("PULL: sync bypassed; the system is read-only.");
                    }
                    return false;
                }
                
                if (serverModeProvider.getServerMode() != ServerMode.PRODUCTION)
                {
                    if (logger.isTraceEnabled())
                    {
                    	logger.trace("PUSH: sync bypassed; not production mode");
                    }
                    return false;
                }
        
                if (!isEnabled())
                {
                    if (logger.isTraceEnabled())
                    {
                        logger.trace("PULL: sync bypassed; tracker not enabled");
                    }
                    return false;
                }
        
                if (!syncAdminService.isOnPremise())
                {
                    //TODO - would be better to not enable job in first place.
                    logger.trace("PULL: sync bypassed; not on premise");
                    return false;
                }
        
                if (!syncAdminService.isEnabled())
                {
                    //TODO - would be better to not enable job in first place.
                    logger.trace("PULL: sync bypassed; sync not enabled");
                    return false;
                }
        
                if (!syncAdminService.hasSyncSetDefintions())
                {
                    //TODO - Would be better to disable the job until one is created
                    logger.trace("PULL: sync bypassed; no SSDs currently defined");
                    return false;
                }

                return true;
            }
        }, false, true);
        
        if (preRequirementsOk == false)
        {
            return;
        }
        
        if (logger.isTraceEnabled())
        {
            logger.trace("PULL: starting");
        }
        
        try
        {
            // Lock the pull
            QName lockQName = QName.createQName(SyncModel.SYNC_MODEL_1_0_URI, "pullJob");
            String lockToken = jobLockService.getLock(lockQName, LOCK_TIME_TO_LIVE, 0, 1);
            TrackerJobLockRefreshCallback callback = new TrackerJobLockRefreshCallback();
            
            jobLockService.refreshLock(lockToken, lockQName, LOCK_REFRESH_TIME, callback);
            
            try
            {
                if (logger.isTraceEnabled())
                {
                    logger.trace("PULL: job lock held");
                }
                
                AuthenticationUtil.runAsSystem(new RunAsWork<Void>()
                {
                   public Void doWork() throws Exception
                   {
                       try
                       {
                           pullImpl();
                           return null;
                       }
                       catch (RemoteSystemUnavailableException re)
                       {
                           if (testAndSetRemoteSystemUnavailable())
                           {
                               // First detection of a remote system unavailable
                               logger.error("PULL: unable to pull :" + re.getMessage());
                            }
                            return null;
                       }
                   }
                });  // end run as system
            }
            finally
            {
                if (logger.isTraceEnabled())
                {
                
                    logger.trace("PULL: job finished");
                }
                // Release the locks on the job and stop refreshing
                callback.isActive = false;
                jobLockService.releaseLock(lockToken, lockQName);
            }
        }
        catch (LockAcquisitionException e)
        {
            if (logger.isDebugEnabled())
            {
                // already running or repo read only
                logger.debug("PULL: cannot obtain lock for job - probably already running");
            }
        }
    }
    
    /**
     * 
     */
    private void pullImpl() throws RemoteSystemUnavailableException
    {
        retryingTransactionHelper.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            @Override
            public Void execute() throws Throwable
            {
                // obtain list of sync sets to push
                String srcRepoId = syncAuditService.getRepoId();
        
                syncSetsToPull.clear();
        
                List<String> changedSyncSets = null;
                // This try/catch is a temporary work-around while pullChangedSSDs is not returning
                // RemoteSystemUnavailableException  
                try
                {
                    // running as system here
                    changedSyncSets = syncSetDefinitionTransport.pullChangedSSDs(srcRepoId);
                }
                catch(AlfrescoRuntimeException are)
                {
                    Throwable t = are.getCause();
                    if(t != null)
                    {
                        if (t instanceof IOException)
                        {
                            throw new RemoteSystemUnavailableException(srcRepoId);
                        }
                    }
                    throw are;
                }
                
                // MNT-9884 related check
                if (null == changedSyncSets)
                {
                    throw new RemoteSystemUnavailableException(srcRepoId);
                }
        
                // If we get here then we communicated  
                setRemoteSystemAvailable();
        
                if (logger.isTraceEnabled()) 
                {
                    logger.trace("PULL: Retrieved " + changedSyncSets.size() + " sync sets for srcRepoId: "+srcRepoId+" ["+changedSyncSets+"]");
                }
                else if (logger.isDebugEnabled() && (changedSyncSets.size() > 0))
                { 
                    logger.debug("PULL: Retrieved " + changedSyncSets.size() + " sync sets for srcRepoId: "+srcRepoId);
                }
        
                for (String syncSet : changedSyncSets)
                {
                    if(!syncSetsToPull.contains(syncSet))
                    {
                        syncSetsToPull.add(syncSet);
                    }
                }
        
                return null;
            }
        }, false, true);
        
        if (syncSetsToPull.isEmpty())
        {
            // short-circuit - nothing to do
            return;
        }
        
        final String systemUserName = AuthenticationUtil.getSystemUserName();
        final String tenantDomain = TenantService.DEFAULT_DOMAIN;
        
        RunAsWorker worker = new RunAsWorker(systemUserName, tenantDomain, "pullSyncSet")
        {
            @Override
            public void doWork(String ssdId) throws Exception
            {
                try
                {
                    pullSyncSet(ssdId);
                }
                catch (AuthenticationException ae)
                {
                    // The sync set authentication is no good.
                    // The other sync sets may work though.
                    logger.error("PULL: Unable to authenticate to pull sync set", ae);
                    //TODO - prevent hundreds of exception messages
                }
            }
        };
        
        final Iterator<String> ssdItr = syncSetsToPull.iterator();
        
        BatchProcessWorkProvider<String> provider = new BatchProcessWorkProvider<String>()
        {
            @Override
            public int getTotalEstimatedWorkSize()
            {
                return syncSetsToPull.size();
            }
            
            @Override
            public Collection<String> getNextWork()
            {
                int batchCount = 0;
                
                List<String> siteBatch = new ArrayList<String>(SSD_BATCH_SIZE);
                while (ssdItr.hasNext() && (batchCount++ != SSD_BATCH_SIZE))
                {
                    siteBatch.add(ssdItr.next());
                }
                return siteBatch;
            }
        };
        
        final RetryingTransactionHelper txHelper = transactionService.getRetryingTransactionHelper();
        txHelper.setMaxRetries(0);
        
        BatchProcessor<String> processor = new BatchProcessor<String>(
                "SyncTrackerPull",
                txHelper,
                provider,
                PULL_THREAD_CNT, SSD_BATCH_SIZE,
                null,
                logger, 100);
        
        // go parallel here - process each sync set
        processor.process(worker, true);
        
        if (processor.getTotalErrors() > 0)
        {
            logger.info("PULL: batch of syncSets processed with "+processor.getTotalErrors()+" errors");
        }
    }
    
    /**
     * Pull a sync set
     * @param syncSetId
     * @throws AuthenticationException
     */
    private void pullSyncSet(final String syncSetId)
    {
        long startTime = System.currentTimeMillis();
        
        if (logger.isDebugEnabled())
        {
            logger.debug("PULL: start syncSet '"+syncSetId+"'");
        }
        
        //
        // Transaction 2: get the sync set (including sync creator and target network)
        //
        final SyncSetDefinition ssd = retryingTransactionHelper.doInTransaction(new RetryingTransactionCallback<SyncSetDefinition>()
        {
            @Override
            public SyncSetDefinition execute() throws Throwable
            {
                SyncSetDefinition ssd = syncAdminService.getSyncSetDefinition(syncSetId);
                
                if (ssd == null)
                {
                    if (logger.isWarnEnabled())
                    {
                        logger.warn("PULL: Cannot pull sync set changes ("+syncSetId+") - sdd not found: "+ssd);
                    }
                    
                    // TODO remove audit from cloud system ...
                }
                else
                {
                    // TODO - what if the sync set creator is removed from the local system - JIRA raised to consider requirements. ALF14377
                    String syncCreatorSource = ssd.getSyncCreator();
                    if(syncCreatorSource == null)
                    {
                        if (logger.isWarnEnabled())
                        {
                            logger.warn("PULL: Cannot pull sync set changes ("+syncSetId+") - person: '"+syncCreatorSource + "'");
                        }
                        ssd = null;
                    }
                    
                    if (! personService.personExists(syncCreatorSource))
                    {
                        if (logger.isWarnEnabled())
                        {
                            logger.warn("PULL: Cannot pull sync set changes ("+syncSetId+") - person not found: '" + syncCreatorSource + "'");
                        }
                        // TODO consider this error - is it ever valid?
                        ssd = null;
                    }
                }
                
                return ssd;
            }
        }, true, true);
        
        if (ssd == null)
        {
            // nothing more to do here
            return;
        }
        
        final String syncCreatorSource = ssd.getSyncCreator();
        final String cloudNetwork = ssd.getRemoteTenantId();
        
        if (cloudNetwork == null)
        {
            // TODO - should this be a more serious error?
            // And should we clean up the audit log if it contains obsolete garbage?
            if (logger.isDebugEnabled())
            {
                logger.debug("PULL: cloud network cannot be found for syncSetId: "+syncSetId);
            }
            return;
        }
        
        AuthenticationUtil.pushAuthentication();
        try
        {
            // note: runAs block would set auditable property to be System
            AuthenticationUtil.setFullyAuthenticatedUser(syncCreatorSource);
            
            //
            // Transaction 3: for this sync set - get the changes
            //
            List<NodeRef> remoteNodeRefs = retryingTransactionHelper.doInTransaction(new RetryingTransactionCallback<List<NodeRef>>()
            {
               @Override
               public List<NodeRef> execute() throws Throwable
               {
                   return syncSetDefinitionTransport.pullChangedNodesForSSD(syncSetId, cloudNetwork);
               }
            });
            
            int pullCnt = 0;
            // now pull the changes for each node
            for (NodeRef remoteNodeRef : remoteNodeRefs)
            {
                try
                {
                    pullNode(ssd, remoteNodeRef, cloudNetwork);
                    pullCnt++;
                }
                catch (RemoteConnectorServerException re)
                {
                    // Something has gone wrong on the back end - may succeed for next node though
                    logger.error("Unable to pull node from target (tenant="+cloudNetwork+", ssdId="+ssd.getId()+", targetNodeRef="+remoteNodeRef+")", re);
                }
                catch (Throwable t)
                {
                    // Something has gone wrong on the back end - may succeed for next node though
                    logger.error("Unable to pull node from target (tenant="+cloudNetwork+", ssdId="+ssd.getId()+", targetNodeRef="+remoteNodeRef+")", t);
                }
            }
            
            if (logger.isInfoEnabled())
            {
            	int pullFailCnt = remoteNodeRefs.size()-pullCnt;
                logger.info("PULL: pull syncSet '"+syncSetId+"' nodes ("+pullCnt+" succeeded, "+pullFailCnt+" failed) [in "+(System.currentTimeMillis()-startTime)+" ms]");
            }
        }
        catch (RemoteConnectorServerException re)
        {
            // Something has gone wrong on the back end - may succeed for the next sync set though
            logger.error("Unable to pull changes from target - syncSetId: "+syncSetId, re);
        }
        finally
        {
            AuthenticationUtil.popAuthentication();
        }
    }
    
    /**
     * Pull a single node
     * 
     * @param syncSetId
     * @param remoteNodeRef
     * @param cloudNetwork (aka. target tenantDomain)
     * 
     * @throws AuthenticationException
     * @throws RemoteSystemUnavailableException
     */ 
    private void pullNode(final SyncSetDefinition ssd, final NodeRef remoteNodeRef, final String cloudNetwork)
    {
    	long startTime = System.currentTimeMillis();
    	
        if (logger.isTraceEnabled())
        {
            logger.trace("PULL: remoteNodeRef: " +remoteNodeRef);
        }
        
        final SyncNodeChangesInfoImpl stubLocal = new SyncNodeChangesInfoImpl(null, remoteNodeRef, cloudNetwork, null);
        
        /**
         * Pull the changes for the remoteNodeRef
         */
        final SyncNodeChangesInfo syncNode =
        retryingTransactionHelper.doInTransaction(new RetryingTransactionCallback<SyncNodeChangesInfo>()
        {
            @Override
            public SyncNodeChangesInfo execute() throws Throwable
            {
                try
                {
                    return memberNodeTransport.pullSyncChange(stubLocal, cloudNetwork);
                }
                catch (SyncNodeException se)
                {
                    if(logger.isDebugEnabled())
                    {
                        logger.debug("Caught sync node exception from pullSyncChange", se); 
                    }
                    /**
                     * currently only valid sync node exception is where the node no longer exists
                     * on the back end.   In which case the back end should clean its own audit logs
                     */
                    //TODO Mark local node if for the same sync set however don't have the node ref!
                    //TODO Change to not throw sync node exception - and handle deleted events here.
                    
                    return null;
                }
            }
        });
        
        if (syncNode == null)
        {
            if (logger.isInfoEnabled())
            {
                logger.info("PULL: No changes pulled for remoteNodeRef: " + remoteNodeRef);
            }
            return;
        }
        
        final AuditToken token = syncNode.getAuditToken();
        
        setRemoteSystemAvailable();
        
        boolean conflict = false;
        NodeRef localNodeRef = null;
        
        if ((token.getChangeType() == null) || 
            token.getChangeType().equals(SsmnChangeType.CREATE) ||
            token.getChangeType().equals(SsmnChangeType.UPDATE)
           )
        {
            /**
             * Apply the remote changes to this repo
             */
            try
            {
                localNodeRef = retryingTransactionHelper.doInTransaction(new RetryingTransactionCallback<NodeRef>()
                {
                    @Override
                    public NodeRef execute() throws Throwable
                    {
                        NodeRef nodeRef = null;
                        try
                        {
                            if (token.getChangeType() != null)
                            {
                                if ((! token.getChangeType().equals(SsmnChangeType.CREATE)) && (syncNode.getLocalNodeRef() == null))
                                {
                                    // TODO when / how often do we get into this state ?
                                    // eg. pull of target create was not correctly confirmed prior to another pull of target update.
                                    if (logger.isWarnEnabled())
                                    {
                                        logger.warn("PULL: missing sourceNodeRef for event: "+token.getChangeType());
                                    }
                                    
                                    if (syncNode.getRemoteNodeRef() != null)
                                    {
                                        NodeRef resultNodeRef = lookupSourceNodeRef(ssd, syncNode.getRemoteNodeRef());
                                        if (resultNodeRef != null)
                                        {
                                            ((SyncNodeChangesInfoImpl)syncNode).setLocalNodeRef(resultNodeRef);
                                        }
                                    }
                                }
                                
                                if ((token.getChangeType().equals(SsmnChangeType.CREATE)) && (syncNode.getLocalParentNodeRef() == null))
                                {
                                    // TODO when / how often do we get into this state ?
                                    // eg. pull of target parent create was not correctly confirmed prior to another pull of target child create.
                                    if (logger.isWarnEnabled())
                                    {
                                        logger.warn("PULL: missing sourceParentNodeRef for event: "+token.getChangeType());
                                    }
                                    
                                    if (syncNode.getRemoteParentNodeRef() != null)
                                    {
                                        NodeRef resultNodeRef = lookupSourceNodeRef(ssd, syncNode.getRemoteParentNodeRef());
                                        if (resultNodeRef != null)
                                        {
                                            if (logger.isWarnEnabled())
                                            {
                                                logger.warn("PULL: found matching sourceParentNodeRef '"+resultNodeRef+"' for event:"+token.getChangeType());
                                            }
                                            ((SyncNodeChangesInfoImpl)syncNode).setLocalParentNodeRef(resultNodeRef);
                                        }
                                    }
                                }
                            }
                            
                            if(logger.isDebugEnabled())
                            {
                                logger.debug("changeType:" +token.getChangeType().toString() + ", localNodeRef:"+syncNode.getLocalNodeRef() + ", remoteNodeRef:" + syncNode.getRemoteNodeRef());
                            }
                            
                            nodeRef = memberNodeTransport.fetchLocalDetailsAndApply(syncNode, false);
                        }
                        catch (SyncNodeException se) 
                        {
                            /**
                             * Swallow sync node exception here.
                             * 
                             * Considered cases:
                             *    Where the local (on premise) node is in a different sync set or none at all.
                             */
                            //TODO review once the exceptions are finalised whether there are any that should be handled differently.
                            if(logger.isDebugEnabled())
                            {
                                logger.debug("swallowing sync node exception", se);
                            }
                        }
                        return nodeRef;
                    }
                });
            }
            catch (ConcurrentModificationException ce)
            {
                if (logger.isInfoEnabled())
                {
                    logger.info("pullNode: "+ce.getMessage());
                }
                conflict = true;
            }
        }
        else if (token.getChangeType().equals(SsmnChangeType.DELETE))
        {
        	final boolean isDeleteOnPrem = ssd.isDeleteOnPrem();
        	ssd.getIncludeSubFolders();
        	
            // deal with a delete on cloud
            retryingTransactionHelper.doInTransaction(new RetryingTransactionCallback<NodeRef>()
            {
            	
                @Override
                public NodeRef execute() throws Throwable
                {
                    NodeRef nodeRef = syncNode.getLocalNodeRef();
                    if ((nodeRef == null) && (syncNode.getRemoteNodeRef() != null))
                    {
                        nodeRef = lookupSourceNodeRef(ssd, syncNode.getRemoteNodeRef());
                    }
                              
                    if (nodeRef != null)
                    {
                        if (nodeService.exists(nodeRef))
                        {
                        	if(isDeleteOnPrem && (permissionService.hasPermission(nodeRef, PermissionService.DELETE) == AccessStatus.ALLOWED))
                        	{
                        		// delete pulled and isDeleteOnPrem is enabled - however if we don't have permission then we need to mark the node
                        		if (logger.isDebugEnabled())
                        		{
                        			logger.debug("pullNode: deleteNode clear otherNodeRef: "+nodeRef);
                        		}
                        		nodeService.deleteNode(nodeRef);
                        	}
                        	else
                        	{
                        		// Not delete on prem which the default setting
                        		
                        		// treat this as an error, if that doesn't fly then we will need to add another aspect
                        	    nodeService.setProperty(nodeRef, SyncModel.PROP_SYNC_REQUESTED, false);
                                Map<QName, Serializable> props = new HashMap<QName, Serializable>(2);
                                props.put(SyncModel.PROP_SYNCED_FAILED_CODE, SyncNodeException.SyncNodeExceptionType.DELETED_ON_CLOUD.getMessageId());
                                props.put(SyncModel.PROP_SYNCED_FAILED_DETAILS, "Node deleted on cloud");
                                props.put(SyncModel.PROP_SYNCED_FAILED_TIME, new Date());   
                                nodeService.addAspect(nodeRef, SyncModel.ASPECT_SYNC_FAILED, props);
                        		
                        		nodeService.setProperty(nodeRef, SyncModel.PROP_OTHER_NODEREF_STRING, null);
                                      
                        		if (logger.isDebugEnabled())
                        		{
                        			logger.debug("pullNode: deleted on cloud - mark as failed, clear otherNodeRef: " + nodeRef);
                        		}
                        	}
                        }
                    }
                              
                    return null;
                }
            });
        }
        else if (token.getChangeType().equals(SsmnChangeType.REMOVE))
        {
                // we do not specifically pull unsync events however we should try to clear the "otherNodeRef" (to allow option to re-sync)
                retryingTransactionHelper.doInTransaction(new RetryingTransactionCallback<NodeRef>()
                {
                   @Override
                   public NodeRef execute() throws Throwable
                   {
                       NodeRef nodeRef = syncNode.getLocalNodeRef();
                       if ((nodeRef == null) && (syncNode.getRemoteNodeRef() != null))
                       {
                           nodeRef = lookupSourceNodeRef(ssd, syncNode.getRemoteNodeRef());
                       }
                       
                       if (nodeRef != null)
                       {
                           if (nodeService.exists(nodeRef))
                           {
                               nodeService.setProperty(nodeRef, SyncModel.PROP_OTHER_NODEREF_STRING, null);
                               
                               if (logger.isDebugEnabled())
                               {
                                   logger.debug("pullNode: clear otherNodeRef: "+nodeRef);
                               }
                           }
                       }
                       
                       return null;
                   }
                });
            
        }
        
        final NodeRef sourceNodeRef = (syncNode.getLocalNodeRef() != null ? syncNode.getLocalNodeRef() : localNodeRef);
        if (token.getOtherNodeRef() == null)
        {
            ((AuditTokenImpl)token).setOtherNodeRef(sourceNodeRef);
        }
        
        if (conflict)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("PULL: conflict detected (sourceNodeRef="+sourceNodeRef+",targetNodeRef=" +remoteNodeRef+")");
            }
            
            retryingTransactionHelper.doInTransaction(new RetryingTransactionCallback<Void>()
            {
                @Override
                public Void execute() throws Throwable
                {
                    /**
                     * Force update - regardless of conflict 
                     * Strategy is cloud wins
                     * 
                     * If node does not exist then there's nothing to update, but we still 
                     * need to drain audit logs
                     */
                    if (nodeService.exists(sourceNodeRef))
                    {
                        syncService.forceUpdate(syncNode);
                    }
                    
                    List<SyncChangeEvent> conflicts = syncAuditService.queryByNodeRef(sourceNodeRef, 500);
                    
                    /**
                      *  Delete local audit log here
                      */
                    final long auditEntryIds[] = new long[conflicts.size()];
                    int i = 0;
                    for(SyncChangeEvent event : conflicts)
                    {
                        auditEntryIds[i++]=event.getAuditId();
                    }
                    
                    deleteAuditEntriesImpl(auditEntryIds);
                    return null;
                }
            });
            
            if (logger.isDebugEnabled())
            {
                logger.debug("PULL: node pulled (sourceNodeRef="+sourceNodeRef+", remoteNodeRef="+remoteNodeRef+") [in "+(System.currentTimeMillis()-startTime)+" ms]");
            }
        }
        
        /**
         * If we get this far then we have successfully pulled the node change, even if we have had a conflict 
         */
        retryingTransactionHelper.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            @Override
            public Void execute() throws Throwable
            {
                // Need to ack the pull so remote audit log can be cleared (and otherNodeRef updated, if needed - eg. create on target)
                memberNodeTransport.confirmPull(new AuditToken[] {token}, cloudNetwork);
                return null;
            }
        });
        
        if (logger.isDebugEnabled())
        {
            logger.debug("PULL: confirmed node pulled (sourceNodeRef="+sourceNodeRef+",remoteNodeRef="+remoteNodeRef+",auditToken="+token+")");
        }
    }
    
    
    private NodeRef lookupSourceNodeRef(SyncSetDefinition ssd, NodeRef remoteNodeRef)
    {
        if(logger.isDebugEnabled())
        {
            logger.debug("looking up localNodeRef from remoteNodeRef:" + remoteNodeRef);
        }
        
        NodeRef resultNodeRef = null;
        for (NodeRef memberNodeRef : syncAdminService.getMemberNodes(ssd))
        {
            String str = (String)nodeService.getProperty(memberNodeRef, SyncModel.PROP_OTHER_NODEREF_STRING);
            if (str != null)
            {
                if (remoteNodeRef.equals(new NodeRef(str)))
                {
                    resultNodeRef = memberNodeRef;
                    
                    if (logger.isInfoEnabled())
                    {
                        logger.info("Found matching sourceNodeRef '"+resultNodeRef+"' for remoteNodeRef:"+remoteNodeRef);
                    }
                    
                    break;
                }
            }
        }
        return resultNodeRef;
    }
    
    /**
     * Called when a remote system is unavailable
     * @return true if this invocation set the flag to true.
     */
    private boolean testAndSetRemoteSystemUnavailable()
    {
        if(remoteSystemStatus == RemoteStatus.DOWN)
        {
            // still unavailable
            return false;
        }
        
        boolean winner = false;
        // Need to set unavailable
        synchronized(this)
        {
            if(remoteSystemStatus != RemoteStatus.DOWN)
            {
                remoteSystemStatus = RemoteStatus.DOWN;
                winner = true;
            }
        }
        
        if(winner)
        {
            retryingTransactionHelper.doInTransaction(new RetryingTransactionCallback<Void>()
            {
               @Override
               public Void execute() throws Throwable
               {
                        if(!attributeService.exists(SyncPropertyInterceptor.keys))
                        {
                            attributeService.createAttribute("sync.communications.error", SyncPropertyInterceptor.keys);
                        }
                        return null;
                    }
             }, false, true);
        }
        
        return winner;
    }
    
    /**
     * Called when a remote system is available
     */
    private void setRemoteSystemAvailable()
    {
        if(remoteSystemStatus == RemoteStatus.UP)
        {
            // remote system was available and is still available
            return;
        }
     
        boolean winner = false;
        // remote system was unavailable but is now available
        synchronized(this)
        {
            if(remoteSystemStatus != RemoteStatus.UP)
            {
                remoteSystemStatus = RemoteStatus.UP;
                winner = true;
            }
        }
        
        if (winner)
        {
            if (logger.isInfoEnabled())
            {
                // this was the winning reset thread 
                logger.info("Remote system is available");
            }
            
            retryingTransactionHelper.doInTransaction(new RetryingTransactionCallback<Void>()
            {
                @Override
                public Void execute() throws Throwable
                {
                    if(attributeService.exists(SyncPropertyInterceptor.keys))
                    {
                        attributeService.removeAttribute(SyncPropertyInterceptor.keys);
                    }
                    return null;
                }
            }, false, true);
        }
    }

    public void setCloudSyncMemberNodeTransport(CloudSyncMemberNodeTransport transportService)
    {
        this.memberNodeTransport = transportService;
    }

    public CloudSyncMemberNodeTransport getCloudSyncMemberNodeTransport()
    {
        return memberNodeTransport;
    }

    public void setJobLockService(JobLockService jobLockService)
    {
        this.jobLockService = jobLockService;
    }

    public JobLockService getJobLockService()
    {
        return jobLockService;
    }
    
    public void setSyncAuditService(SyncAuditService syncAuditService)
    {
        this.syncAuditService = syncAuditService;
    }

    public SyncAuditService getSyncAuditService()
    {
        return syncAuditService;
    }

    public void setRetryingTransactionHelper(RetryingTransactionHelper retryingTransactionHelper)
    {
        this.retryingTransactionHelper = retryingTransactionHelper;
    }

    public RetryingTransactionHelper getRetryingTransactionHelper()
    {
        return retryingTransactionHelper;
    }

    public void setSsmnChangeManagement(SsmnChangeManagement ssmnChangeManagement)
    {
        this.ssmnChangeManagement = ssmnChangeManagement;
    }

    public SsmnChangeManagement getSsmnChangeManagement()
    {
        return ssmnChangeManagement;
    }

    public void setSyncAdminService(SyncAdminService syncAdminService)
    {
        this.syncAdminService = syncAdminService;
    }

    public SyncAdminService getSyncAdminService()
    {
        return syncAdminService;
    }

    public void setPersonService(PersonService service)
    {
        this.personService = service;
    }
    
    public PersonService getPersonService()
    {
        return personService;
    }
    
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }
    
    public NodeService getNodeService()
    {
        return nodeService;
    }
    
    public void setCheckOutCheckInService(CheckOutCheckInService cociService)
    {
        this.cociService = cociService;
    }
    
    public CheckOutCheckInService getCheckOutCheckInService()
    {
        return cociService;
    }
    
    public void setCloudSyncSetDefinitionTransport(CloudSyncSetDefinitionTransport syncSetDefinitionTransport)
    {
        this.syncSetDefinitionTransport = syncSetDefinitionTransport;
    }
    
    public CloudSyncSetDefinitionTransport getCloudSyncSyncSetDefinitionTransport()
    {
        return syncSetDefinitionTransport;
    }
    
    public void setSyncService(SyncService syncService)
    {
        this.syncService = syncService;
    }
    
    public SyncService getSyncService()
    {
        return syncService;
    }
    
    public void setTransactionService(TransactionService transactionService)
    {
        this.transactionService = transactionService;
    }
    
    public TransactionService getTransactionService()
    {
        return transactionService;
    }
    
    /**
     * Enables and disables this sync tracker component.
     * <p>
     * If not enabled then the push and pull jobs will not run.
     * <p>
     * @param enabled enable the sync tracker component.
     */
    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    public void setLockService(LockService lockService)
    {
        this.lockService = lockService;
    }

    public LockService getLockService()
    {
        return lockService;
    }

    public void setAttributeService(AttributeService attributeService)
    {
        this.attributeService = attributeService;
    }

    public AttributeService getAttributeService()
    {
        return attributeService;
    }

    public void setPermissionService(PermissionService permissionService)
    {
        this.permissionService = permissionService;
    }

    public PermissionService getPermissionService()
    {
        return permissionService;
    }

    public ServerModeProvider getServerModeProvider() {
		return serverModeProvider;
	}

	public void setServerModeProvider(ServerModeProvider serverModeProvider) {
		this.serverModeProvider = serverModeProvider;
	}

    public void setBehaviourFilter(BehaviourFilter behaviourFilter)
    {
        this.behaviourFilter = behaviourFilter;
    }

    public BehaviourFilter getBehaviourFilter()
    {
        return behaviourFilter;
    }

	private class TrackerJobLockRefreshCallback
        implements JobLockRefreshCallback
    {
        public boolean isActive = true;
        
        @Override
        public boolean isActive()
        {
            return isActive;
        }

        @Override
        public void lockReleased()
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("lock released");
            }
        }
    };
}
