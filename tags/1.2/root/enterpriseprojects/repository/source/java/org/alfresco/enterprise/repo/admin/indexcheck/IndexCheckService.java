/*
 * Copyright 2005-2010 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.admin.indexcheck;

import java.util.Date;

import org.alfresco.service.cmr.repository.NodeRef;


/**
 * Index check service interface
 * <p/>
 * Check for DB txn ids that might out-of-sync with the local indexes
 * <p/>
 * Notes: 
 * - assumes indexes are local to the machine (repo app server) on which the checks are run. 
 * - in case of clustered environment, should be re-run for each machine (repo app server) in the cluster
 * 
 * @author janv
 */
public interface IndexCheckService 
{
    /**
     * Get ip address for this machine (repo app server)
     */
    public String getIpAddress();
    
    /**
     * Get last check report run (since server started - ie. not persisted)
     */
    public IndexTxnInfo getLastReportRun();
    
    /**
     * Check last txn id
     */
	public IndexTxnInfo checkLastTxn();
	
	/**
     * Check given txn id
     */
	public IndexTxnInfo checkTxn(long txnId);
	
	/**
     * Check txns ids from given txn id
     */
	public IndexTxnInfo checkTxnsFromTxn(long fromTxnId);
	
	/**
	 * Check txns ids from given datetime
	 */
	public IndexTxnInfo checkTxnsFromTime(Date fromTime);
       
	/**
     * Check txns ids from given txn id to given txn id (exclusive)
     */
	public IndexTxnInfo checkTxnsFromToTxn(long fromTxnId, long toTxnId);
	
	/**
	 * Check txns ids from/to given datetimes
	 */
	public IndexTxnInfo checkTxnsFromToTime(Date fromTime, Date toTime);
	  
    /**
     * Check all txns ids
     */
    public IndexTxnInfo checkAllTxns();
    
	/**
	 * For given node, get node status (from shared DB) and associated index txn info (from local index)
	 */
	public IndexTxnInfo getStatusForNode(NodeRef nodeRef);
	
	/**
     * For given txn, get node status for changed nodes (from shared DB) and associated index txn info (from local index)
     */
	public IndexTxnInfo getStatusForTxnNodes(long txnId);
	
	/**
	 * Reindex from given txn id
	 * <p/>
	 * Note: will be ignored if index tracker is already running
	 */
	public Thread reindexFromTxn(long txnId);
	
	/**
     * Reindex from given datetime
     * <p/>
     * Note: will be ignored if index tracker is already running
     */
	public Thread reindexFromTime(Date fromTime);
	
	/**
	 * Get reindex summary progress (of background Index Transaction Tracker)
	 */
	public String getReindexProgress();
}
