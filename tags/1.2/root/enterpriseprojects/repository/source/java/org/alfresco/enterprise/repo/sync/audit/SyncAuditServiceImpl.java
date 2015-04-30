/*
 * Copyright 2005-2012 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.sync.audit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.alfresco.enterprise.repo.sync.SyncSetDefinition;
import org.alfresco.enterprise.repo.sync.audit.SyncEventHandler.AuditEventId;
import org.alfresco.enterprise.repo.sync.transport.AuditToken;
import org.alfresco.enterprise.repo.sync.transport.impl.AuditTokenImpl;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.service.cmr.audit.AuditQueryParameters;
import org.alfresco.service.cmr.audit.AuditService;
import org.alfresco.service.cmr.audit.AuditService.AuditQueryCallback;
import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.descriptor.Descriptor;
import org.alfresco.service.descriptor.DescriptorService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.ParameterCheck;
import org.alfresco.util.PropertyMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * See {@link SyncAuditService}
 * 
 * @author Neil Mc Erlean, janv
 * @since 4.1
 */
public class SyncAuditServiceImpl implements SyncAuditService
{
    private static final Log logger = LogFactory.getLog(SyncAuditServiceImpl.class);
    
    private AuditService auditService;
    private DescriptorService descriptorService;
    private SyncEventHandler syncEventHandler;
    
    public void setAuditService(AuditService service)
    {
        this.auditService = service;
    }
    
    public void setDescriptorService(DescriptorService descriptorService)
    {
        this.descriptorService = descriptorService;
    }
    
    public void setSyncEventHandler(SyncEventHandler eventHandler)
    {
        this.syncEventHandler = eventHandler;
    }
    
    private String repoId;
    
    // helper method
    @Override public String getRepoId()
    {
        if (repoId == null)
        {
            Descriptor repoDesc = descriptorService.getCurrentRepositoryDescriptor();
            if (repoDesc == null)
            {
                throw new AlfrescoRuntimeException("Unable to get repository descriptor");
            }
            repoId = repoDesc.getId();
            if (repoId == null)
            {
                throw new AlfrescoRuntimeException("Unable to determine repository id");
            }
        }
        return repoId;
    }
    
    @Override public void recordContentPropertyUpdate(NodeRef nodeRef, ContentData beforeValue, ContentData afterValue)
    {
        syncEventHandler.persistAuditChangesContentChanged(nodeRef, afterValue.getContentUrl());
    }
    
    @Override public void recordNonContentPropertiesUpdate(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after)
    {
        // 'changed' here will mean added, removed or edited/changed.
        // We don't (yet) distinguish.
        HashSet<QName> changedProps = new HashSet<QName>();
        changedProps.addAll(PropertyMap.getAddedProperties(before, after).keySet());
        changedProps.addAll(PropertyMap.getRemovedProperties(before, after).keySet());
        changedProps.addAll(PropertyMap.getChangedProperties(before, after).keySet());
        
        syncEventHandler.persistAuditChangesPropertiesChanged(nodeRef, changedProps);
    }
    
    @Override public void recordSsdDeleted(NodeRef nodeToBeDeleted)
    {
        syncEventHandler.persistAuditChanges(AuditEventId.SSD_TO_DELETE, nodeToBeDeleted);
    }
    
    @Override public void recordAspectAdded(NodeRef nodeRef, QName aspectTypeQName)
    {
        syncEventHandler.persistAuditChangesAspectAdded(nodeRef, aspectTypeQName);
    }
    
    @Override public void recordAspectRemoved(NodeRef nodeRef, QName aspectTypeQName)
    {
        syncEventHandler.persistAuditChangesAspectRemoved(nodeRef, aspectTypeQName);
    }
    
    @Override public void recordSsmnAdded(NodeRef newMemberNode)
    {
        syncEventHandler.persistAuditChanges(AuditEventId.SSMN_ADDED, newMemberNode);
    }
    
    @Override public void recordSsmnUpdateAll(NodeRef newMemberNode)
    {
        syncEventHandler.persistAuditChanges(AuditEventId.SSMN_UPDATE_ALL, newMemberNode);
    }
    
    @Override public void recordSsmnRemoved(SyncSetDefinition ssd, NodeRef formerMemberNode)
    {
        // called when SSMN association *was* removed (=> OnDeleteAssociationPolicy)
        syncEventHandler.persistAuditChanges(AuditEventId.SSMN_REMOVED, formerMemberNode, ssd, true);
    }
    
    @Override public void recordSsmnDeleted(SyncSetDefinition ssd, NodeRef formerMemberNode)
    {
        // called when SSMN node *will be* deleted (=> BeforeDeleteNodePolicy / BeforeMoveNodePolicy)
        syncEventHandler.persistAuditChanges(AuditEventId.SSMN_DELETED, formerMemberNode);
    }
    
    @Override public void recordSsmnMoved(SyncSetDefinition ssd, NodeRef memberNode)
    {
        syncEventHandler.persistAuditChanges(AuditEventId.SSMN_MOVED, memberNode);
    }
    
    @Override public void deleteAuditEntries(long[] auditEntryIds)
    {
        List<Long> ids = new ArrayList<Long>(auditEntryIds.length);
        for (long l : auditEntryIds)
        {
            ids.add(new Long(l));
        }
        
        if (logger.isDebugEnabled())
        {
            logger.debug("Clearing audit entries from sync audit: " + ids);
        }
        
        auditService.clearAudit(ids);
    }
    
    @Override public void deleteAuditEntries(AuditToken...tokens)
    {
        int size = 0;
        for (AuditToken token : tokens)
        {
            size += ((AuditTokenImpl)token).getAuditIds().length;
        }
        
        long[] ids = new long[size];
        size = 0;
        for (AuditToken token : tokens)
        {
            long[] tids = ((AuditTokenImpl)token).getAuditIds();
            System.arraycopy(tids, 0, ids, size, tids.length);
            size += tids.length;
        }
        
        deleteAuditEntries(ids);
    }
    
    @Override public List<SyncChangeEvent> queryByNodeRef(NodeRef nodeRef, int maxResults)
    {
        final AuditQueryParameters params = new AuditQueryParameters();
        params.setApplicationName(SyncEventHandler.AUDIT_APPLICATION_NAME);
        params.addSearchKey(SyncEventHandler.PATH_TO_NODEREF_KEY, nodeRef.toString());
        
        return queryImpl(params, maxResults);
    }
    
    @Override public List<SyncChangeEvent> queryBySsdId(String ssdId, int maxResults)
    {
        ParameterCheck.mandatory("ssdId", ssdId);
        
        final AuditQueryParameters params = new AuditQueryParameters();
        params.setApplicationName(SyncEventHandler.AUDIT_APPLICATION_NAME);
        params.addSearchKey(SyncEventHandler.PATH_TO_SSDID_KEY, ssdId);
        
        return queryImpl(params, maxResults);
    }
    
    @Override public List<String> querySsdManifest(final String srcRepoId, int maxResults)
    {
        ParameterCheck.mandatory("srcRepoId", srcRepoId);
        
        final AuditQueryParameters params = new AuditQueryParameters();
        params.setApplicationName(SyncEventHandler.AUDIT_APPLICATION_NAME);
        params.addSearchKey(SyncEventHandler.PATH_TO_REPOID_KEY, srcRepoId);
        
        final List<String> results = new ArrayList<String>();
        
        auditService.auditQuery(new AuditQueryCallback()
        {
            @Override public boolean valuesRequired() { return true; }
            
            @Override public boolean handleAuditEntryError(Long entryId, String errorMsg, Throwable error)
            {
                if (logger.isWarnEnabled())
                {
                    logger.warn("Error fetching SSD summary entry ('"+srcRepoId+") - " + errorMsg, error);
                }
                return false;
            }
            
            @Override public boolean handleAuditEntry(Long entryId, String applicationName, String user, long time, Map<String, Serializable> values)
            {
                // TODO: optimise down to AuditService - eg. to query for a single/distinct value set
                String ssdId = (String)values.get(SyncEventHandler.PATH_TO_SSDID_KEY);
                if (! results.contains(ssdId))
                {
                    results.add(ssdId);
                }
                return true;
            }
        }, params, maxResults);
        
        if (logger.isTraceEnabled())
        {
            logger.trace("Fetched SSD summary change log ('"+srcRepoId+") - "+results);
        }
        else if (logger.isDebugEnabled() && (results.size() > 0))
        {
            logger.debug("Fetched SSD summary change log ('"+srcRepoId+") - "+results);
        }
        
        return results;
    }
    
    @Override public int clearAudit()
    {
        return auditService.clearAudit(SyncEventHandler.AUDIT_APPLICATION_NAME, null, null);
    }
    
    private List<SyncChangeEvent> queryImpl(final AuditQueryParameters params, int maxResults)
    {
        final List<SyncChangeEvent> results = new ArrayList<SyncChangeEvent>();
        
        auditService.auditQuery(new AuditQueryCallback()
        {
            @Override public boolean valuesRequired() { return true; }
            
            @Override public boolean handleAuditEntryError(Long entryId, String errorMsg, Throwable error)
            {
                logger.warn("Error fetching sync update entry - " + errorMsg, error);
                return false;
            }
            
            @Override public boolean handleAuditEntry(Long entryId, String applicationName, String user, long time, Map<String, Serializable> values)
            {
                results.add(new SyncChangeEventImpl(entryId, user, time, values));
                return true;
            }
        }, params, maxResults);
        return results;
    }
}