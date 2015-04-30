/*
 * Copyright (C) 2005-2011 Alfresco Software Limited.
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.repo.tenant.TenantUtil;
import org.alfresco.service.cmr.attributes.AttributeService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Tenant Quota Service implementation.
 */
public class TenantQuotaServiceImpl implements TenantQuotaService
{
    // Logger
    private static Log logger = LogFactory.getLog(TenantQuotaServiceImpl.class);
    
    private AttributeService attributeService;
    private static final String ATTR_KEY_QUOTA_ROOT = ".tenantQuotas";

    private Map<String, QuotaUsage> usages = new HashMap<String, QuotaUsage>();
    private Map<String, List<QuotaListener>> quotaListeners = new HashMap<String, List<QuotaListener>>();

    
    public void setAttributeService(AttributeService service)
    {
        this.attributeService = service;
    }
    
    @Override
    public void registerUsage(String quotaName, QuotaUsage usage)
    {
        usages.put(quotaName, usage);
        if (usage instanceof QuotaListener)
        {
            registerQuotaListener(quotaName, (QuotaListener)usage);
        }
        
        if (logger.isInfoEnabled())
            logger.info("Registered usage for quota " + quotaName);
    }
    
    @Override
    public long getQuota(String quotaName)
    {
        Long quota = (Long) attributeService.getAttribute(ATTR_KEY_QUOTA_ROOT, TenantUtil.getCurrentDomain(), quotaName);
        return (quota == null ? UNKNOWN : quota);
    }
    
    @Override
    public void setQuota(String quotaName, long newQuota)
    {
        attributeService.setAttribute(newQuota, ATTR_KEY_QUOTA_ROOT, TenantUtil.getCurrentDomain(), quotaName);
        fireOnQuotaChangedEvent(quotaName);
        
        if (logger.isInfoEnabled())
            logger.info("Set quota: name=" + quotaName + ", quota=" + newQuota + " ["+TenantUtil.getCurrentDomain()+"]");
    }

    @Override
    public void clearQuota(String quotaName)
    {
        attributeService.removeAttribute(ATTR_KEY_QUOTA_ROOT, TenantUtil.getCurrentDomain(), quotaName);
        fireOnQuotaChangedEvent(quotaName);
        
        if (logger.isInfoEnabled())
            logger.info("Cleared quota: name=" + quotaName + " ["+TenantUtil.getCurrentDomain()+"]");
    }

    @Override
    public QuotaUsage getUsage(String quotaName)
    {
        return usages.get(quotaName);
    }

    private void fireOnQuotaChangedEvent(String quotaName)
    {
        List<QuotaListener> listeners = quotaListeners.get(quotaName);
        if (listeners != null)
        {
            for (QuotaListener listener : listeners)
            {
                listener.onQuotaChanged(quotaName);
            }
        }
    }
    
    private void registerQuotaListener(String quotaName, QuotaListener listener)
    {
        // NOTE: Assumption that registration takes place prior event firing, so no concurrency protection here
        List<QuotaListener> listeners = quotaListeners.get(quotaName);
        if (listeners == null)
        {
            listeners = new ArrayList<QuotaListener>();
            quotaListeners.put(quotaName, listeners);
        }
        if (!listeners.contains(listener))
        {
            listeners.add(listener);
        }
    }
    
}
