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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.ibatis.IdsEntity;
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
import org.alfresco.repo.tenant.TenantUtil.TenantRunAsWork;
import org.alfresco.repo.transaction.AlfrescoTransactionSupport;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.repo.transaction.TransactionListenerAdapter;
import org.alfresco.service.cmr.attributes.AttributeService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.Pair;
import org.alfresco.util.PropertyCheck;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationEvent;
import org.springframework.extensions.surf.util.AbstractLifecycleBean;

/**
 * Implement type count - usage and quota for nodes of given type (per-tenant)
 * 
 * @author janv
 * @since Thor
 */
public class TypeCountUsageImpl extends AbstractLifecycleBean
    implements InitializingBean, QuotaUsage, QuotaListener, 
               NodeServicePolicies.BeforeDeleteNodePolicy, NodeServicePolicies.OnCreateNodePolicy,
               NodeServicePolicies.OnAddAspectPolicy, NodeServicePolicies.OnRemoveAspectPolicy
{
    private static Log logger = LogFactory.getLog(TypeCountUsageImpl.class);
    
    private long lockTTL = 30000L;
    private QName lockQName;
    
    private QName typeQName;
    private List<QName> excludeAspectQNames = Collections.emptyList(); // exclude nodes from count that have one or more of these aspects
    private List<QName> includeAspectQNames = Collections.emptyList(); // include nodes from count that have one or more of these aspects
    
    private String quotaName;
    
    public static final String KEY_TYPE_COUNT_PREFIX = "TypeCountUsageImpl.typeCount-";
    
    private String txnTypeKey;
    
    private static final String QUERY_NS = "alfresco.query.tenant_usages";
    private static final String QUERY_SELECT_COUNT_TYPE = "select_CountType"; // within tenant
    private static final String QUERY_SELECT_COUNT_TYPE_WITH_ASPECTS = "select_CountTypeWithAspect"; // within tenant
    
    public static final String ATTR_KEY_USAGE_ROOT = ".tenantUsages";
    
    public static final String ATTR_KEY_USAGE_TYPE_COUNT_USAGE_PREFIX = "typeCountUsage-";
    private String attrTypeUsageKey;
    
    private PolicyComponent policyComponent;
    private JobLockService jobLockService;
    private AttributeService attributeService;
    private CannedQueryDAO cannedQueryDAO;
    private QNameDAO qnameDAO;
    private NodeDAO nodeDAO;
    private TenantService tenantService;
    private TenantQuotaService tenantQuotaService;
    private NodeService nodeService;
    private TransactionService transactionService;
    
    private boolean enabled = true;
    
    private int fixedUsageAdjustment = 0;
    
    private TypeCountTransactionListener typeCountTransactionListener;
    
    public void setPolicyComponent(PolicyComponent policyComponent)
    {
        this.policyComponent = policyComponent;
    }
    
    public void setJobLockService(JobLockService jobLockService)
    {
        this.jobLockService = jobLockService;
    }
    
    public void setAttributeService(AttributeService attributeService)
    {
        this.attributeService = attributeService;
    }
    
    public void setCannedQueryDAO(CannedQueryDAO cannedQueryDAO)
    {
        this.cannedQueryDAO = cannedQueryDAO;
    }
    
    public void setQnameDAO(QNameDAO qnameDAO)
    {
        this.qnameDAO = qnameDAO;
    }
    
    public void setNodeDAO(NodeDAO nodeDAO)
    {
        this.nodeDAO = nodeDAO;
    }
    
    public void setTenantService(TenantService tenantService)
    {
        this.tenantService = tenantService;
    }
    
    public void setTenantQuotaService(TenantQuotaService tenantQuotaService)
    {
        this.tenantQuotaService = tenantQuotaService;
    }
    
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }
    
    public void setTransactionService(TransactionService transactionService)
    {
        this.transactionService = transactionService;
    }
    
    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }
    
    public void setLockTTL(long lockTTL)
    {
        this.lockTTL = lockTTL;
    }
    
    public void setTypeQName(String typeQnameStr)
    {
        this.typeQName = QName.createQName(typeQnameStr);
    }
    
    public void setQuotaName(String quotaName)
    {
        this.quotaName = quotaName;
    }
    
    public void setExcludeAspectQNames(List<String> excludeAspectQnameStrs)
    {
        if (includeAspectQNames.size() + excludeAspectQnameStrs.size() > 2)
        {
            throw new AlfrescoRuntimeException("Cannot exclude more than 1 aspect when including an aspect: "+excludeAspectQnameStrs);
        }
        if (excludeAspectQnameStrs.size() > 2)
        {
            throw new AlfrescoRuntimeException("Cannot exclude more than 2 aspects: "+excludeAspectQnameStrs);
        }
        
        excludeAspectQNames = new ArrayList<QName>(excludeAspectQnameStrs.size());
        for (String excludeAspectQnameStr : excludeAspectQnameStrs)
        {
            excludeAspectQNames.add(QName.createQName(excludeAspectQnameStr));
        }
    }

    public void setIncludeAspectQNames(List<String> includeAspectQnameStrs)
    {
        if (excludeAspectQNames.size() > 1)
        {
            throw new AlfrescoRuntimeException("Cannot include aspect when excluding 2 aspects: "+includeAspectQnameStrs);
        }
        if (includeAspectQnameStrs.size() > 1)
        {
            throw new AlfrescoRuntimeException("Cannot include more than 1 aspects: "+includeAspectQnameStrs);
        }
        
        includeAspectQNames = new ArrayList<QName>(includeAspectQnameStrs.size());
        for (String includeAspectQnameStr : includeAspectQnameStrs)
        {
            includeAspectQNames.add(QName.createQName(includeAspectQnameStr));
        }
    }

    public void setFixedUsageAdjustment(int fixedUsageAdjustment)
    {
        this.fixedUsageAdjustment = fixedUsageAdjustment;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void afterPropertiesSet() throws Exception
    {
        PropertyCheck.mandatory(this, "policyComponent", policyComponent);
        PropertyCheck.mandatory(this, "jobLockService", jobLockService);
        PropertyCheck.mandatory(this, "attributeService", attributeService);
        PropertyCheck.mandatory(this, "cannedQueryDAO", cannedQueryDAO);
        PropertyCheck.mandatory(this, "qnameDAO", qnameDAO);
        PropertyCheck.mandatory(this, "tenantService", tenantService);
        
        PropertyCheck.mandatory(this, "typeQName", typeQName);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onBootstrap(ApplicationEvent event)
    {
        if (enabled)
        {
            // Register interest in the beforeDeleteNode policy - for node deletion of given type
            policyComponent.bindClassBehaviour(
                    QName.createQName(NamespaceService.ALFRESCO_URI, "beforeDeleteNode"),
                    typeQName,
                    new JavaBehaviour(this, "beforeDeleteNode"));
            
            // Register interest in the onCreateNode policy - for node creation of given type
            policyComponent.bindClassBehaviour(
                    QName.createQName(NamespaceService.ALFRESCO_URI, "onCreateNode"),
                    typeQName,
                    new JavaBehaviour(this, "onCreateNode"));

            for (QName includeAspectQName : includeAspectQNames)
            {
                // Register interest in the onAddAspect policy
                policyComponent.bindClassBehaviour(
                        QName.createQName(NamespaceService.ALFRESCO_URI, "onAddAspect"),
                        includeAspectQName,
                        new JavaBehaviour(this, "onAddAspect"));
                
                // Register interest in the onRemoveAspect policy
                policyComponent.bindClassBehaviour(
                        QName.createQName(NamespaceService.ALFRESCO_URI, "onRemoveAspect"),
                        includeAspectQName,
                        new JavaBehaviour(this, "onRemoveAspect"));
            }

            for (QName excludeAspectQName : excludeAspectQNames)
            {
                // Register interest in the onAddAspect policy
                policyComponent.bindClassBehaviour(
                        QName.createQName(NamespaceService.ALFRESCO_URI, "onAddAspect"),
                        excludeAspectQName,
                        new JavaBehaviour(this, "onAddAspect"));
                
                // Register interest in the onRemoveAspect policy
                policyComponent.bindClassBehaviour(
                        QName.createQName(NamespaceService.ALFRESCO_URI, "onRemoveAspect"),
                        excludeAspectQName,
                        new JavaBehaviour(this, "onRemoveAspect"));
            }

            typeCountTransactionListener = new TypeCountTransactionListener();
            
            if ((quotaName == null) || (quotaName.length() == 0))
            {
                quotaName = typeQName.toString();
            }
            
            txnTypeKey = KEY_TYPE_COUNT_PREFIX+quotaName;
            attrTypeUsageKey = ATTR_KEY_USAGE_TYPE_COUNT_USAGE_PREFIX+quotaName;
            
            lockQName = QName.createQName(NamespaceService.SYSTEM_MODEL_1_0_URI, KEY_TYPE_COUNT_PREFIX+quotaName);
            
            tenantQuotaService.registerUsage(quotaName, this);
        }
    }
    
    @Override
    protected void onShutdown(ApplicationEvent event)
    {
        // NOOP
    }
    
    public void onCreateNode(ChildAssociationRef childAssocRef)
    {
        NodeRef nodeRef = childAssocRef.getChildRef();
        if (logger.isTraceEnabled())
        {
            logger.trace("onCreateNode: countName="+quotaName+", type="+typeQName+", nodeRef="+nodeRef+")");
        }
        
        if (excludedNode(nodeRef))
        {
            return;
        }
        
        // increment in-txn delta
        int txnDelta = updateTxnTypeCount(1);
        checkQuota(txnDelta);
    }
    
    private void checkQuota(int txnDelta)
    {
        long typeCountUsage = getUsage();
        long typeCountQuota = getQuota();
        
        // check whether quota exceeded
        if ((typeCountQuota >= TenantQuotaService.QUOTA_ZERO) && (typeCountUsage + txnDelta > typeCountQuota))
        {
            if (logger.isWarnEnabled())
            {
                logger.warn("Count quota exceeded: "+
                              "countName=" + quotaName +
                              ", type=" + typeQName +
                              ", usage=" + typeCountUsage +
                              ", quota=" + typeCountQuota);
            }
            // TODO i18n - note: may need to configure different messages per type
            throw new AlfrescoRuntimeException("Quota exceeded");
        }
    }
    
    public void beforeDeleteNode(NodeRef nodeRef)
    {
        if (logger.isTraceEnabled())
        {
            logger.trace("beforeDeleteNode: countName="+quotaName+" type="+typeQName+", nodeRef="+nodeRef+")");
        }
        
        if (excludedNode(nodeRef))
        {
            return;
        }
        
        // decrement in-txn delta
        updateTxnTypeCount(-1);
    }
    
    @Override
    public void onAddAspect(NodeRef nodeRef, QName aspectTypeQName)
    {
        if (!nodeService.getType(nodeRef).equals(typeQName))
        {
            return;
        }

        if (includeAspectQNames.contains(aspectTypeQName))
        {
            // increment in-txn delta
            int txnDelta = updateTxnTypeCount(1);
            checkQuota(txnDelta);
        }
        else if (excludeAspectQNames.contains(aspectTypeQName))
        {
            // decrement in-txn delta
            updateTxnTypeCount(-1);
        }
    }
    
    @Override
    public void onRemoveAspect(NodeRef nodeRef, QName aspectTypeQName)
    {
        if (!nodeService.getType(nodeRef).equals(typeQName))
        {
            return;
        }

        if (includeAspectQNames.contains(aspectTypeQName))
        {
            // decrement in-txn delta
            updateTxnTypeCount(-1);
        }
        else if (excludeAspectQNames.contains(aspectTypeQName))
        {
            // increment in-txn delta
            updateTxnTypeCount(1);
        }
        
    }
    
    private boolean excludedNode(NodeRef nodeRef)
    {
        if (excludeAspectQNames.size() > 0 || includeAspectQNames.size() > 0)
        {
            Set<QName> nodeAspects = nodeService.getAspects(nodeRef);
            for (QName excludeAspectQName : excludeAspectQNames)
            {
                if (nodeAspects.contains(excludeAspectQName))
                {
                    // excluded
                    return true;
                }
            }
            for (QName includeAspectQName : includeAspectQNames)
            {
                if (!nodeAspects.contains(includeAspectQName))
                {
                    // excluded
                    return true;
                }
            }
        }
        
        return false;
    }
    
    private Map<String, Integer> getTxnTypeCount()
    {
        Map<String, Integer> txnTypeCountDelta = AlfrescoTransactionSupport.getResource(txnTypeKey);
        if (txnTypeCountDelta == null)
        {
            txnTypeCountDelta = new HashMap<String, Integer>(1);
        }
        return txnTypeCountDelta;
    }
    
    private int updateTxnTypeCount(int delta)
    {
        Map<String, Integer> txnTypeCountDelta = getTxnTypeCount();
        String tenantDomain = TenantUtil.getCurrentDomain();
        Integer txnCount = txnTypeCountDelta.get(tenantDomain);
        
        txnCount = (txnCount == null ? delta : txnCount + delta);
        
        txnTypeCountDelta.put(tenantDomain, txnCount);
        
        AlfrescoTransactionSupport.bindResource(txnTypeKey, txnTypeCountDelta);
        AlfrescoTransactionSupport.bindListener(typeCountTransactionListener);
        
        return txnCount;
    }
    
    class TypeCountTransactionListener extends TransactionListenerAdapter
    {
        @Override
        public void afterCommit()
        {
            // normally within 1 tenant (but does not have to be - eg. unit test)
            Map<String, Integer> txnTypeCountDelta = getTxnTypeCount();
            
            if (txnTypeCountDelta.size() > 0)
            {
                for (final String tenantDomain : txnTypeCountDelta.keySet())
                {
                    // run in new txn to ensure shared cache (propertyUniqueContext) is updated (since existing txn is marked as closed in afterCommit)
                    RetryingTransactionCallback<Object> updateCounts = new RetryingTransactionCallback<Object>()
                    {
                        public Object execute() throws Throwable
                        {
                            TenantUtil.runAsTenant(new TenantRunAsWork<Object>()
                            {
                                public Object doWork() throws Exception
                                {
                                    updateTypeCount();
                                    return null;
                                }
                            }, tenantDomain);
                            return null;
                        }
                    };
                    transactionService.getRetryingTransactionHelper().doInTransaction(updateCounts, false, true);
                }
            }
        }
    }
    
    public boolean getEnabled()
    {
        return enabled;
    }
    
    @Override
    public long getUsage()
    {
        Long typeCount = (Long) attributeService.getAttribute(ATTR_KEY_USAGE_ROOT, TenantUtil.getCurrentDomain(), attrTypeUsageKey);
        return (typeCount == null ? TenantQuotaService.UNKNOWN : typeCount);
    }
    
    @Override
    public boolean isOverQuota()
    {
        long typeCountUsage = getUsage();
        long typeCountQuota = getQuota();
        
        // check whether quota exceeded
        if ((typeCountQuota >= TenantQuotaService.QUOTA_ZERO) && (typeCountUsage > typeCountQuota))
        {
            return true;
        }
        return false;
    }

    private long getQuota()
    {
        return tenantQuotaService.getQuota(quotaName);
    }

    @Override
    public void onQuotaChanged(String quotaName)
    {
        // force (re-)count
        updateTypeCount(); 
    }
    
    /**
     * Update type count (for current tenant) with appropriate locking
     * 
     * note: based on RepoUsageComponentImpl - could rationalise in future (also need to distinguish between system-wide & tenant-specific usages)
     */
    private boolean updateTypeCount()
    {
        long startTime = 0L;
        if (logger.isDebugEnabled())
        {
            startTime = System.currentTimeMillis();
        }
        
        QName tenantlockQName = QName.createQName(lockQName.getNamespaceURI(), lockQName.getLocalName() + "." + TenantUtil.getCurrentDomain());
        String lockToken = null;
        try
        {
            // Lock to prevent concurrent queries
            lockToken = jobLockService.getLock(tenantlockQName, lockTTL, 20, 5);

            Long typeQNameId = qnameDAO.getOrCreateQName(typeQName).getFirst();
            
            StoreRef tenantStoreRef = tenantService.getName(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
            Pair<Long, StoreRef> tenantStorePair = nodeDAO.getStore(tenantStoreRef);
            final Long storeId;
            if (tenantStorePair == null)
            {
                if (logger.isTraceEnabled())
                {
                    logger.trace("No store found for tenant: " + tenantStoreRef);
                }
                storeId = Long.valueOf(-1L);
            }
            else
            {
                storeId = tenantStorePair.getFirst();
            }

            IdsEntity idsParam = new IdsEntity();
            idsParam.setIdOne(storeId);
            idsParam.setIdTwo(typeQNameId);
            
            String query = QUERY_SELECT_COUNT_TYPE;
            
            if (includeAspectQNames.size() == 0)
            {
                if (excludeAspectQNames.size() > 0)
                {
                    idsParam.setIdThree(qnameDAO.getOrCreateQName(excludeAspectQNames.get(0)).getFirst());
                    if (excludeAspectQNames.size() > 1)
                    {
                        idsParam.setIdFour(qnameDAO.getOrCreateQName(excludeAspectQNames.get(1)).getFirst());
                    }
                }
            }
            else
            {
                idsParam.setIdThree(qnameDAO.getOrCreateQName(includeAspectQNames.get(0)).getFirst());
                if (excludeAspectQNames.size() > 0)
                {
                    idsParam.setIdFour(qnameDAO.getOrCreateQName(excludeAspectQNames.get(0)).getFirst());
                }
                query = QUERY_SELECT_COUNT_TYPE_WITH_ASPECTS;
            }
            
            // Count nodes of given type (specific to tenant - hence parameterised by store)
            Long typeCount = cannedQueryDAO.executeCountQuery(QUERY_NS, query, idsParam);
            
            // Adjust usage (-ve or +ve) if configured
            typeCount += fixedUsageAdjustment;
            
            // Lock again to be sure we still have the right to update
            jobLockService.refreshLock(lockToken, tenantlockQName, lockTTL);
            
            attributeService.setAttribute(
                    typeCount,
                    ATTR_KEY_USAGE_ROOT, TenantUtil.getCurrentDomain(), attrTypeUsageKey);
            
            if (logger.isDebugEnabled())
            {
                logger.debug("Count calculated: countName="+quotaName+", type="+typeQName+", usage="+typeCount+", quota="+getQuota()+" ["+TenantUtil.getCurrentDomain()+"] in "+(System.currentTimeMillis()-startTime)+" ms");
            }
            
            // Success
            return true;
        }
        catch (LockAcquisitionException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Failed to get lock for "+typeQName+" counts: " + e.getMessage());
            }
            return false;
        }
        finally
        {
            if (lockToken != null)
            {
                jobLockService.releaseLock(lockToken, tenantlockQName);
            }
        }
    }

}
