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
package org.alfresco.enterprise.repo.management;

import java.util.Date;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.TabularData;

import org.alfresco.enterprise.repo.admin.indexcheck.SOLRAclInfo;
import org.alfresco.enterprise.repo.admin.indexcheck.SOLRAclTransactionInfo;
import org.alfresco.enterprise.repo.admin.indexcheck.SOLRIndexCheckService;
import org.alfresco.enterprise.repo.admin.indexcheck.SOLRIndexInfo;
import org.alfresco.enterprise.repo.admin.indexcheck.SOLRNodeInfo;
import org.alfresco.enterprise.repo.admin.indexcheck.SOLRTransactionNodeInfo;
import org.alfresco.enterprise.repo.admin.indexcheck.SOLRTransactionReport;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedOperationParameters;
import org.springframework.jmx.export.annotation.ManagedResource;

/**
 * An implementation of the {@link SOLRIndexMBean} management interface that retrieves its information from a Solr
 * instance.
 * 
 * @since 4.0
 */
@ManagedResource
public class SOLRIndex implements SOLRIndexMBean
{
    private String core;

    private SOLRIndexCheckService solrService;

    private ThreadLocal<SOLRIndexInfo> indexInfo = new ThreadLocal<SOLRIndexInfo>();

    public SOLRIndex(String core, SOLRIndexCheckService solrService)
    {
        this.core = core;
        this.solrService = solrService;
    }

    /**
     * Call Solr to get the index info for this Solr core. This is stored in a thread local to allow a single Solr call
     * to fulfil each attribute exported from this bean. The thread local is refreshed if it is older than 1000ms.
     * 
     * @return
     */
    protected SOLRIndexInfo getIndexInfo()
    {
        SOLRIndexInfo info = indexInfo.get();
        if (info == null || (System.currentTimeMillis() - info.getTimestamp() > 1000))
        {
            info = solrService.indexInfo(core);
            indexInfo.set(info);
        }

        return info;
    }

    @ManagedAttribute(description = "The location of the index configuration")
    public String getInstanceDirectory()
    {
        SOLRIndexInfo indexInfo = getIndexInfo();
        return indexInfo.getInstanceDirectory();
    }

    @ManagedAttribute(description = "The location of the index data drectory")
    public String getDataDirectory()
    {
        SOLRIndexInfo indexInfo = getIndexInfo();
        return indexInfo.getDataDirectory();
    }

    @ManagedAttribute(description = "The SOLR server start up")
    public Date getStartTime()
    {
        SOLRIndexInfo indexInfo = getIndexInfo();
        return indexInfo.getStartTime();
    }

    @ManagedAttribute(description = "The SOLR server up time")
    public Long getUptime()
    {
        SOLRIndexInfo indexInfo = getIndexInfo();
        return indexInfo.getUptime();
    }

    @ManagedAttribute(description = "The number of documents in the index")
    public Integer getNumDocuments()
    {
        SOLRIndexInfo indexInfo = getIndexInfo();
        return indexInfo.getNumDocuments();
    }

    @ManagedAttribute(description = "The maximum number of documents in the index")
    public Integer getMaxDocument()
    {
        SOLRIndexInfo indexInfo = getIndexInfo();
        return indexInfo.getMaxDocument();
    }

    @ManagedAttribute(description = "The version")
    public Long getVersion()
    {
        SOLRIndexInfo indexInfo = getIndexInfo();
        return indexInfo.getVersion();
    }

    @ManagedAttribute(description = "Is the index optimized")
    public Boolean getOptimized()
    {
        SOLRIndexInfo indexInfo = getIndexInfo();
        return indexInfo.getOptimized();
    }

    @ManagedAttribute(description = "Is the index curent")
    public Boolean getCurrent()
    {
        SOLRIndexInfo indexInfo = getIndexInfo();
        return indexInfo.getCurrent();
    }

    @ManagedAttribute(description = "Does the index have deletions")
    public Boolean getHasDeletions()
    {
        SOLRIndexInfo indexInfo = getIndexInfo();
        return indexInfo.getHasDeletions();
    }

    @ManagedAttribute(description = "The index instance directory")
    public String getIndexInstanceDirectory()
    {
        SOLRIndexInfo indexInfo = getIndexInfo();
        return indexInfo.getIndexInstanceDirectory();
    }

    @ManagedAttribute(description = "The date the index was last modified")
    public Date getLastModified()
    {
        SOLRIndexInfo indexInfo = getIndexInfo();
        return indexInfo.getLastModified();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @ManagedOperation(description = "Build a general report for transactions and acl change sets")
    @ManagedOperationParameters({ 
            @ManagedOperationParameter(name = "fromTxnId", description = "The transaction from which to start reporting (may be null)"),
            @ManagedOperationParameter(name = "toTxnId", description = "The transaction at which to stop reporting (may be null)"),
            @ManagedOperationParameter(name = "fromAclChangeSetId", description = "The acl change set from which to start reporting (may be null)"),
            @ManagedOperationParameter(name = "fromAclChangeSetId", description = "The acl change set at which to stop reporting (may be null)"),
            @ManagedOperationParameter(name = "fromTimeISO8601", description = "The time from which to start reporting (may be null) in ISO-86012 format"),
            @ManagedOperationParameter(name = "toTimeISO8601", description = "The time at which to stop reporting (may be null) in ISO-86012 format")

    })
    public CompositeData transactionReport(long fromTxnId, long toTxnId, long fromAclChangeSetId, long toAclChangeSetId, String fromTimeISO8601, String toTimeISO8601) throws OpenDataException
    {
        SOLRTransactionReport txnReport = solrService.transactionReport(core, fromTxnId, toTxnId, fromAclChangeSetId, toAclChangeSetId, fromTimeISO8601, toTimeISO8601);
        return txnReport.getCompositeData();
        // return txnReport.getTabularData();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @ManagedOperation(description = "Build a detailed report for the given acl change set id")
    @ManagedOperationParameters({ @ManagedOperationParameter(name = "aclChangeSetId", description = "The acl change set database id")

    })
    public TabularData transactionAcls(long aclChangeSetId) throws OpenDataException
    {
        SOLRAclTransactionInfo info = solrService.aclTransactionReport(core, aclChangeSetId);
        return info.getAclsTabularData();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @ManagedOperation(description = "Build a detailed report for the given transaction id")
    @ManagedOperationParameters({ @ManagedOperationParameter(name = "txnId", description = "The transaction database id")

    })
    public TabularData transactionNodes(long txnId) throws OpenDataException
    {
        SOLRTransactionNodeInfo info = solrService.transactionNodesReport(core, txnId);
        return info.getNodesTabularData();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @ManagedOperation(description = "Build a detailed report for the given node Id")
    @ManagedOperationParameters({ @ManagedOperationParameter(name = "nodeDbId", description = "The node database id")

    })
    public CompositeData nodeReport(long dbid) throws OpenDataException
    {
        SOLRNodeInfo info = solrService.nodeReport(core, dbid);
        return info.getCompositeData();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @ManagedOperation(description = "Build a detailed report for the given acl Id")
    @ManagedOperationParameters({ @ManagedOperationParameter(name = "aclId", description = "The acl database id")

    })
    public CompositeData aclReport(long aclId) throws OpenDataException
    {
        SOLRAclInfo info = solrService.aclReport(core, aclId);
        return info.getCompositeData();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.enterprise.repo.management.SOLRIndexMBean#fix()
     */
    @Override
    @ManagedOperation(description = "Check the index for missing, duplicated, and extra transactions or acl change sets and fix up any found")
    public void checkAndFixIndex()
    {
        solrService.checkAndFixIndex(core);
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.enterprise.repo.management.SOLRIndexMBean#backUpIndex(java.lang.String)
     */
    @Override
    @ManagedOperation(description = "Backup the index")
    @ManagedOperationParameters({ @ManagedOperationParameter(name = "remoteLocation", description = "The remote location for the index back up")

    })
    public void backUpIndex(String remoteLocation)
    {
        solrService.backUpIndex(core, remoteLocation);
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.enterprise.repo.management.SOLRIndexMBean#rebuildIndexCache()
     */
    @Override
    @ManagedOperation(description = "Rebuild the index cache for ACL -> leaf, AUX doc -> leaf and owner look-up")
    public void rebuildIndexCache()
    {
        solrService.rebuildIndexCache(core);
    }
}
