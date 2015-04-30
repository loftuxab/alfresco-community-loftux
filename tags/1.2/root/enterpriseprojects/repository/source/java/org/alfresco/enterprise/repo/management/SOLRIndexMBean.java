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

import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedOperationParameters;
import org.springframework.jmx.export.annotation.ManagedResource;

/**
 * A Management Interface exposing properties and operations of a Solr index core for monitoring.
 * 
 * @since 4.0
 */

public interface SOLRIndexMBean
{
    public String getInstanceDirectory();

    public String getDataDirectory();

    public Date getStartTime();

    public Long getUptime();

    public Integer getNumDocuments();

    public Integer getMaxDocument();

    public Long getVersion();

    public Boolean getOptimized();

    public Boolean getCurrent();

    public Boolean getHasDeletions();

    public String getIndexInstanceDirectory();

    public Date getLastModified();

    /**
     * Check the transactions in the index that match the criteria specified by the parameters
     * 
     * @param fromTxnId
     * @param toTxnId
     * @param fromAclTxnId
     * @param toAclTxnId
     * @param fromTime
     * @param toTime
     * @return CompositeData containing information about the transaction(s)
     */
    public CompositeData transactionReport(long fromTxnId, long toTxnId, long fromAclTxnId, long toAclTxnId, String fromTimeISO8601, String toTimeISO8601) throws OpenDataException;

    /**
     * Get node information for the given node dbid
     * 
     * @param dbid
     * @return CompositeData containing information about the node
     */
    public CompositeData nodeReport(long dbid) throws OpenDataException;

    /**
     * Get acl information for the given aclid
     * 
     * @param aclid
     * @return CompositeData containing information about the acl
     */
    public CompositeData aclReport(long aclId) throws OpenDataException;

    /**
     * Get the acls contained in the transaction 'txnId'
     * 
     * @param txnId
     * @return TabularData containing information on each acl in the given transaction
     */
    public TabularData transactionAcls(long txnId) throws OpenDataException;

    /**
     * Get the nodes contained in the transaction 'txnId'
     * 
     * @param txnId
     * @return TabularData containing information on each node in the given transaction
     */
    public TabularData transactionNodes(long txnId) throws OpenDataException;

    /**
     * Fix any transaction or acl change set issues in the sole indexes.
     */
    public void checkAndFixIndex();

    /**
     * Create a backup for the index.
     * 
     * @param core
     */
    public void backUpIndex(String remoteLocation);

    /**
     * Rebuild the index cache.
     * 
     * @param core
     */
    public void rebuildIndexCache();
}
