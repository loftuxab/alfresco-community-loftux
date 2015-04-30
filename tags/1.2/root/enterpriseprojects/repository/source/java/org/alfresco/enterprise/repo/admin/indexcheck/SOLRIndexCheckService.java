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
package org.alfresco.enterprise.repo.admin.indexcheck;

import java.util.List;

import org.apache.solr.client.solrj.SolrServerException;

/**
 * Solr index check service interface.
 * 
 * @since 4.0
 *
 */
public interface SOLRIndexCheckService
{
	/**
	 * Get a list of the Solr cores
	 * 
	 * @return a list of Solr core names
	 */
	public List<String> getRegisteredCores();

	/**
	 * Returns meta information about a Solr index core
	 * 
	 * @return metadata pertaining to the Solr index core
	 */
	public SOLRIndexInfo indexInfo(String core);
	   
	/**
	 * Check the transactions
	 * 
	 * @param core the Solr core name
	 * @param fromTxnId from transaction id
	 * @param toTxnId to transaction id
	 * @param fromAclTxnId from acl transaction id
	 * @param toAclTxnId to acl transaction id
	 * @param fromTime from time
	 * @param toTime to time
	 * @return
	 */
	public SOLRTransactionReport transactionReport(String core, long fromTxnId, long toTxnId, long fromAclTxnId,
			long toAclTxnId, String fromTimeISO8601, String toTimeISO8601);
	
	/**
	 * Generates a transaction node report for the given transaction.
	 * 
	 * @param core
	 * @param txnId
	 * @return
	 */
	public SOLRTransactionNodeInfo transactionNodesReport(String core, long txnId);

	/**
	 * Generates a transaction acl report for the given transaction.
	 * 
	 * @param core
	 * @param txnId
	 * @return
	 */
	public SOLRAclTransactionInfo aclTransactionReport(String core, long txnId);

	/**
	 * Generates a node report for the given node dbid.
	 * 
	 * @param core
	 * @param dbId
	 * @return
	 */
	public SOLRNodeInfo nodeReport(String core, long dbId);

	/**
	 * Generates an acl report for the given aclid.
	 * 
	 * @param core
	 * @param txnId
	 * @return
	 */
	public SOLRAclInfo aclReport(String core, long aclId);
	
	/**
	 * Check and fix the index - missing, duplicated or unknown transactions and acl change sets.
	 */
	public void checkAndFixIndex(String core);
	
	/**
	 * Create a backup for the index.
	 * 
	 * @param core
	 */
	public void backUpIndex(String core, String remoteLocation);
	
	/**
	 * Rebuild the index cache.
	 * 
	 * @param core
	 */
	public void rebuildIndexCache(String core);
}
