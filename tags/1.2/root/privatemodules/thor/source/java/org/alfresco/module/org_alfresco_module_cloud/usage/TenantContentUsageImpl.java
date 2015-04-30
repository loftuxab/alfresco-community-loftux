/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.module.org_alfresco_module_cloud.usage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.alfresco.ibatis.IdsEntity;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.domain.node.NodeDAO;
import org.alfresco.repo.domain.qname.QNameDAO;
import org.alfresco.repo.domain.query.CannedQueryDAO;
import org.alfresco.repo.lock.JobLockService;
import org.alfresco.repo.lock.LockAcquisitionException;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.tenant.TenantService;
import org.alfresco.repo.tenant.TenantUtil;
import org.alfresco.repo.transaction.AlfrescoTransactionSupport;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.repo.version.Version2Model;
import org.alfresco.service.cmr.attributes.AttributeService;
import org.alfresco.service.cmr.attributes.AttributeService.AttributeQueryCallback;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.usage.ContentQuotaException;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.session.RowBounds;

/**
 * Implements Tenant Content Usage
 * 
 * @author janv
 * @since Thor
 */
public class TenantContentUsageImpl implements QuotaUsage, QuotaListener, 
    NodeServicePolicies.BeforeDeleteNodePolicy, NodeServicePolicies.OnCreateNodePolicy
{
    private static Log logger = LogFactory.getLog(TenantContentUsageImpl.class);
    
    private static final String KEY_TXN_DELTA_SIZE = "storeContentUsage.deltaSize";
    private static final String ATTR_KEY_USAGE_ROOT = ".tenantUsages";
    private static final String ATTR_KEY_USAGE_DIRTY = ".tenantUsages-DirtyStoreIds";
    private static final String ATTR_KEY_USAGE_FILE_USAGE = "fileUsage";
    
    private static final String QUERY_NS = "alfresco.query.tenant_usages";
    private static final String QUERY_SELECT_STORES_CONTENT_SIZE = "select_GetStoresContentSize"; // multiple stores
    
    private PolicyComponent policyComponent;
    private NodeService nodeService;
    private TenantService tenantService;
    private TenantQuotaService tenantQuotaService;
    private TransactionService transactionService;
    private AttributeService attributeService;
    private NodeDAO nodeDAO;
    private QNameDAO qnameDAO;
    private CannedQueryDAO cannedQueryDAO;
    
    private int updateBatchSize = 100;
    
    private boolean enabled = true;
    
    private Lock writeLock = new ReentrantLock();
    
    private static final QName LOCK = QName.createQName(NamespaceService.SYSTEM_MODEL_1_0_URI, "TenantContentUsageLock");
    private JobLockService jobLockService;
    
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }
    
    public void setTenantService(TenantService tenantService)
    {
        this.tenantService = tenantService;
    }

    public void setTenantQuotaService(TenantQuotaService tenantQuotaService)
    {
        this.tenantQuotaService = tenantQuotaService;
    }

    public void setAttributeService(AttributeService attributeService)
    {
        this.attributeService = attributeService;
    }
    
    public void setPolicyComponent(PolicyComponent policyComponent)
    {
        this.policyComponent = policyComponent;
    }
    
    public void setTransactionService(TransactionService transactionService)
    {
        this.transactionService = transactionService;
    }
    
    public void setNodeDAO(NodeDAO nodeDAO)
    {
        this.nodeDAO = nodeDAO;
    }
    
    public void setUpdateBatchSize(int updateBatchSize)
    {
        this.updateBatchSize = updateBatchSize;
    }
    
    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }
    
    public void setJobLockService(JobLockService jobLockService)
    {
        this.jobLockService = jobLockService;
    }
    
    public void setQnameDAO(QNameDAO qnameDAO)
    {
        this.qnameDAO = qnameDAO;
    }
    
    public void setCannedQueryDAO(CannedQueryDAO cannedQueryDAO)
    {
        this.cannedQueryDAO = cannedQueryDAO;
    }
    
    private StoreRef STOREREF = new StoreRef(StoreRef.PROTOCOL_WORKSPACE, Version2Model.STORE_ID);
    
    public void init()
    {
        if (enabled)
        {
            // Register interest in the beforeDeleteNode policy - for content
            policyComponent.bindClassBehaviour(
                    QName.createQName(NamespaceService.ALFRESCO_URI, "beforeDeleteNode"),
                    ContentModel.TYPE_CONTENT,
                    new JavaBehaviour(this, "beforeDeleteNode"));
            
            // Register interest in the onCreateNode policy - for content
            policyComponent.bindClassBehaviour(
                    QName.createQName(NamespaceService.ALFRESCO_URI, "onCreateNode"),
                    ContentModel.TYPE_CONTENT,
                    new JavaBehaviour(this, "onCreateNode"));
            
            tenantQuotaService.registerUsage(TenantQuotaService.FILE_STORAGE, this);
        }
    }
    
    private void setDirtyFlag(StoreRef storeRef)
    {
        storeRef = tenantService.getName(storeRef);
        
        Pair<Long, StoreRef> storePair = nodeDAO.getStore(storeRef);
        if (storePair == null)
        {
            logger.warn("Could not find store: "+storeRef);
        }
        else
        {
            long storeId = storePair.getFirst();
            Boolean dirty = (Boolean)attributeService.getAttribute(ATTR_KEY_USAGE_DIRTY, storeId);
            if ((dirty == null) || (dirty == false))
            {
                attributeService.setAttribute(true, ATTR_KEY_USAGE_DIRTY, storeId);
                
                if (logger.isDebugEnabled())
                {
                    logger.debug("Mark dirty usage ["+TenantUtil.getCurrentDomain()+"]");
                }
            }
            else
            {
                if (logger.isTraceEnabled())
                {
                    logger.trace("Already marked dirty usage ["+TenantUtil.getCurrentDomain()+"]");
                }
            }
        }        
    }
    
    public void onCreateNode(ChildAssociationRef childAssocRef)
    {
        NodeRef nodeRef = childAssocRef.getChildRef();
        StoreRef storeRef = nodeRef.getStoreRef();
        if (storeRef.equals(STOREREF))
        {
            ContentData contentData = (ContentData)nodeService.getProperty(nodeRef, ContentModel.PROP_CONTENT);
            
            if (contentData != null)
            {
                long contentSize = contentData.getSize();
                
                if (logger.isDebugEnabled())
                {
                    logger.debug("onCreateNode: "+nodeRef+", size="+contentSize+" ["+TenantUtil.getCurrentDomain()+"]");
                }
                
                incrementUsage(storeRef, contentSize);
            }
        }
    }
    
    public void beforeDeleteNode(NodeRef nodeRef)
    {
        StoreRef storeRef = nodeRef.getStoreRef();
        if (storeRef.equals(STOREREF))
        {
            ContentData contentData = (ContentData)nodeService.getProperty(nodeRef, ContentModel.PROP_CONTENT);
            
            if (contentData != null)
            {
                long contentSize = contentData.getSize();
                
                if (logger.isDebugEnabled())
                {
                    logger.debug("beforeDeleteNode: "+nodeRef+", size="+contentSize+" ["+TenantUtil.getCurrentDomain()+"]");
                }
                
                decrementUsage(storeRef, contentSize);
            }
        }
    }
    
    private void incrementUsage(StoreRef storeRef, long contentSize)
    {
        long currentSize = getUsage(); // note: can be -1 if usage has not been calculated
        
        // track multiple increments/decrements in txn
        Long deltaSize = (Long)AlfrescoTransactionSupport.getResource(KEY_TXN_DELTA_SIZE);
        
        if (logger.isTraceEnabled())
        {
            logger.trace("deltaSize="+(deltaSize == null ? "0 (null)" : deltaSize)+", contentSize="+contentSize+" ["+TenantUtil.getCurrentDomain()+"]["+AlfrescoTransactionSupport.getTransactionId()+"]");
        }
        
        if (deltaSize == null)
        {
            deltaSize = 0L;
            setDirtyFlag(storeRef);
        }
        
        deltaSize = deltaSize + contentSize;
        AlfrescoTransactionSupport.bindResource(KEY_TXN_DELTA_SIZE, deltaSize);
        
        long newSize = currentSize + deltaSize;
        long quotaSize = getQuota();
        
        // check whether quota exceeded
        if ((quotaSize > 0) && (newSize > quotaSize))
        {
            if (logger.isWarnEnabled())
            {
                logger.warn("Store (" + storeRef + ") quota exceeded: content=" + deltaSize +
                              ", usage=" + currentSize +
                              ", quota=" + quotaSize + " ["+TenantUtil.getCurrentDomain()+"]");
            }
            // TODO i18n
            throw new ContentQuotaException("Store quota exceeded");
        }
    }
    
    private void decrementUsage(StoreRef storeRef, long contentSize)
    {
        long currentSize = getUsage(); // note: can be -1 if usage has not been calculated
        
        // track multiple increments/decrements in txn
        Long deltaSize = (Long)AlfrescoTransactionSupport.getResource(KEY_TXN_DELTA_SIZE);
        if (deltaSize == null)
        {
            deltaSize = 0L;
            setDirtyFlag(storeRef);
        }
        
        deltaSize = deltaSize - contentSize;
        AlfrescoTransactionSupport.bindResource(KEY_TXN_DELTA_SIZE, deltaSize);
        
        long newSize = currentSize + deltaSize;
        
        if (newSize < 0)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Store (" + storeRef + ") has negative usage (" + newSize + ") ["+TenantUtil.getCurrentDomain()+"]");
            }
        }
    }
    
    // should only be called by update job
    private void setStoreStoredUsage(long storeId, String tenantDomain, long currentUsage)
    {
        attributeService.setAttribute(currentUsage, ATTR_KEY_USAGE_ROOT, tenantDomain, ATTR_KEY_USAGE_FILE_USAGE);
        
        // clear the dirty flag
        attributeService.removeAttribute(ATTR_KEY_USAGE_DIRTY, storeId);
        
        if (logger.isTraceEnabled())
        {
            logger.trace("Set store usage [storeId="+storeId+", tenantDomain="+tenantDomain+", currentUsage="+currentUsage+" ["+TenantUtil.getCurrentDomain()+"]"); // note: current tenant should be empty/null (ie. system)
        }
    }
    
    public boolean getEnabled()
    {
        return enabled;
    }
    
    private List<StoreUsageEntity> recalculateStoresSize(List<Long> storeIds)
    {
        long start = 0L;
        if (logger.isDebugEnabled())
        {
            start = System.currentTimeMillis();
        }
        
        List<StoreUsageEntity> stores = getStoresContentSize(storeIds);
        
        if (logger.isDebugEnabled())
        {
            StringBuilder sb = new StringBuilder(512);
            
            for (StoreUsageEntity store : stores)
            {
                sb.append("[identifier=").append(store.getIdentifier())
                  .append(", usage=").append(store.getStoreSize())
                  .append(", quota=").append(getQuota())
                  .append("]");
            }
            
            logger.debug("Store usages calculated: "+stores.size()+" store(s) ["+sb.toString()+"] in "+(System.currentTimeMillis()-start)+" ms");
        }
        else if (logger.isInfoEnabled())
        {
            logger.info("Store usages calculated: "+stores.size()+" stores");
        }
        
        return stores;
    }
    
    @Override
    public long getUsage()
    {
        Long fileUsage = (Long) attributeService.getAttribute(ATTR_KEY_USAGE_ROOT, TenantUtil.getCurrentDomain(), ATTR_KEY_USAGE_FILE_USAGE);
        return (fileUsage == null ? TenantQuotaService.UNKNOWN : fileUsage);
    }
    
    @Override
    public boolean isOverQuota()
    {
        long currentSize = getUsage();
        if (currentSize == -1)
        {
            // No usage / none known about - can't be over quota
            return false;
        }
        
        long quotaSize = getQuota();
        
        // check whether quota exceeded
        if ((quotaSize > 0) && (currentSize > quotaSize))
        {
            return true;
        }
        return false;
    }

    private long getQuota()
    {
        return tenantQuotaService.getQuota(TenantQuotaService.FILE_STORAGE);
    }
    
    @Override
    public void onQuotaChanged(String quotaName)
    {
        setDirtyFlag(STOREREF);
    }
    
    public void execute()
    {
        if (enabled == false || transactionService.isReadOnly())
        {
            return;
        }
        
        boolean locked = writeLock.tryLock();
        if (locked)
        {
            try
            {
                updateUsages();
            }
            finally
            {
                writeLock.unlock();
            }
        }
    }
    
    private void updateUsages()
    {
        String lockToken = null;
        
        if (jobLockService != null)
        {
            try
            {
                lockToken = jobLockService.getLock(LOCK, 20000L);
            }
            catch (LockAcquisitionException e)
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug("updateUsages: Can't get lock. Assume multiple (store content usage) update jobs: "+e);
                }
                return;
            }
        }
        
        try
        {
            // find dirty stores (note: system-wide - hence could be across multiple tenants)
            List<Long> storeIdsToUpdate = getStoreIdsMarkedForUpdate();
            
            // TODO use batch processor for multi-threaded batch
            List<Long> batchStores = new ArrayList<Long>(updateBatchSize);
            Iterator<Long> storeIterator = storeIdsToUpdate.iterator();
            
            while (storeIterator.hasNext())
            {
                batchStores.add(storeIterator.next());
                
                if (batchStores.size() == updateBatchSize || (! storeIterator.hasNext()))
                {
                    if (lockToken != null)
                    {
                        try
                        {
                            jobLockService.refreshLock(lockToken, LOCK, updateBatchSize * 100L);
                        }
                        catch (LockAcquisitionException e)
                        {
                            // We don't have the lock so just quit (remaining store usages will be updated next time around)
                            if (logger.isDebugEnabled())
                            {
                                logger.debug("updateUsages: Can't refresh lock: "+e);
                            }
                            return;
                        }
                    }
                    
                    updateStoreUsages(batchStores);
                    batchStores.clear();
                }
            }
        }
        finally
        {
            try
            {
                if (lockToken != null ) { jobLockService.releaseLock(lockToken, LOCK); }
            }
            catch (LockAcquisitionException e)
            {
                // Ignore
                if (logger.isDebugEnabled())
                {
                    logger.debug("updateUsages: Can't release lock: "+e);
                }
            }
        }
    }
    
    private void updateStoreUsages(final List<Long> storeIdsToUpdate)
    {
        RetryingTransactionCallback<Void> updateUsage = new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                // calculate new store usages
                List<StoreUsageEntity> stores = recalculateStoresSize(storeIdsToUpdate);
                
                // set the sizes
                for (StoreUsageEntity store : stores)
                {
                    String tenantDomain = tenantService.getDomain(store.getIdentifier(), false);
                    long storeSize = (store.getStoreSize() != null ? store.getStoreSize() : 0L);
                    
                    setStoreStoredUsage(store.getId(), tenantDomain, storeSize);
                }
                
                return null;
            }
        };
        
        // execute in READ-WRITE txn
        transactionService.getRetryingTransactionHelper().doInTransaction(updateUsage, false);
    }
    
    // Calculate store size
    private List<StoreUsageEntity> getStoresContentSize(List<Long> storeIds)
    {
        // minor optimization - qname for ContentModel.TYPE_CONTENT & ContentModel.PROP_CONTENT is the same
        Pair<Long, ? extends Object> contentQNamePair = qnameDAO.getQName(ContentModel.PROP_CONTENT);
        
        if (contentQNamePair == null)
        {
            return Collections.emptyList(); // The static(s) have not been used, so there can be no results
        }
        
        Long contentQNameEntityId = contentQNamePair.getFirst();
        
        IdsEntity idsEntity = new IdsEntity();
        idsEntity.setIdOne(contentQNameEntityId);
        idsEntity.setIds(storeIds);
        
        // Query for the 'new' (FK) style content data properties (stored in 'string_value')
        return cannedQueryDAO.executeQuery(QUERY_NS, QUERY_SELECT_STORES_CONTENT_SIZE, idsEntity, RowBounds.NO_ROW_OFFSET, RowBounds.NO_ROW_LIMIT);
    }
    
    private List<Long> getStoreIdsMarkedForUpdate()
    {
        // Query attributes for stores marked for update (stored as long attribute representing last (re-)mark time)
        final List<Long> storeIdsToUpdate = new ArrayList<Long>();
        
        attributeService.getAttributes(new AttributeQueryCallback()
        {
            public boolean handleAttribute(Long id, Serializable value, Serializable[] keys)
            {
                storeIdsToUpdate.add((Long)keys[1]);
                
                if (logger.isTraceEnabled())
                {
                    logger.trace("Found dirty store " + keys[1]);
                }
                
                return true;
            }
        },
        ATTR_KEY_USAGE_DIRTY);
        
        return storeIdsToUpdate;
    }
}
