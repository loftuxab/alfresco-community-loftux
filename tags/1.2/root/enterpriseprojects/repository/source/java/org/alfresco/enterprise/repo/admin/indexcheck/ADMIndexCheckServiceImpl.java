/*
 * Copyright 2005-2010 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.admin.indexcheck;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.repo.domain.node.NodeDAO;
import org.alfresco.repo.domain.node.Transaction;
import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.alfresco.repo.node.index.AbstractReindexComponent.InIndex;
import org.alfresco.repo.node.index.IndexTransactionTracker;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeRef.Status;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.util.ApplicationContextHelper;
import org.alfresco.util.ParameterCheck;
import org.alfresco.util.SearchLanguageConversion;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.extensions.surf.util.ISO8601DateFormat;

/**
 * Alfresco DM Index check service implementation (also implements script object)
 * 
 * @author janv
 */
public class ADMIndexCheckServiceImpl extends BaseScopableProcessorExtension implements IndexCheckService
{
    private static Log logger = LogFactory.getLog(ADMIndexCheckServiceImpl.class);
    
    private static final int MAX_TRANSACTIONS_PER_ITERATION = 1000;
    
    private final String ipAddress;
    
    private IndexTransactionTracker indexTransactionTracker;
    private NodeDAO nodeDAO;
    protected SearchService searcher;
    
    private final WriteLock indexCheckLock;
    
    private static final String NO_INDEX_CHECK = "No index check in progress";
    
    private static String indexCheckStatusMsg;
    private static IndexTxnInfo lastReportRun;
    
    
    public void setIndexTransactionTracker(IndexTransactionTracker indexTransactionTracker)
    {
        this.indexTransactionTracker = indexTransactionTracker;
    }
    
    public void setNodeDAO(NodeDAO nodeDAO)
    {
        this.nodeDAO = nodeDAO;
    }

    public void setSearcher(SearchService searcher)
    {
        this.searcher = searcher;
    }
    
    /**
     * 
     */
    public ADMIndexCheckServiceImpl()
    {
        try
        {
            this.ipAddress = InetAddress.getLocalHost().getHostAddress();
        }
        catch (UnknownHostException e)
        {
            throw new AlfrescoRuntimeException("Failed to get server IP address", e);
        }
        
        indexCheckLock = new ReentrantReadWriteLock().writeLock();
        
        indexCheckStatusMsg = NO_INDEX_CHECK;
        lastReportRun = null;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.enterprise.repo.admin.IndexCheckService#getIpAddress()
     */
    public String getIpAddress()
    {
        return this.ipAddress;
        
    }
    
    public IndexTxnInfo getLastReportRun()
    {
        return lastReportRun;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.repo.admin.IndexCheckService#checkLastTxn()
     */
	public IndexTxnInfo checkLastTxn()
	{
	    long maxCommitTime = System.currentTimeMillis()+1L;

        // Get the max transaction ID
        final Long maxTxnId = nodeDAO.getMaxTxnIdByCommitTime(maxCommitTime);
        
        // Shortcut
        if (maxTxnId == null)
        {
            logger.warn("No txns found: "+maxCommitTime);
            return null;
        }
        
        String title = "Check last txn (txnId="+maxTxnId+" at "+ISO8601DateFormat.format(new Date(maxCommitTime))+")";
	    return checkTxn(title, maxTxnId, true);
	}
	
	/* (non-Javadoc)
     * @see org.alfresco.enterprise.repo.admin.IndexCheckService#checkTxn(long)
     */
    public IndexTxnInfo checkTxn(long txnId)
    {
        String title = "Check specified txn (txnId="+txnId+" at "+ISO8601DateFormat.format(new Date(System.currentTimeMillis()))+")";       
        return checkTxn(title, txnId, false);
    }
    
    private IndexTxnInfo checkTxn(String title, long txnId, boolean last)
    {
        long runStartTime = System.currentTimeMillis();
        
        logger.info(title);
        
        Transaction txn = nodeDAO.getTxnById(txnId);
        InIndex inIndex = indexTransactionTracker.isTxnPresentInIndex(txn);
        
        long minTxnTime = nodeDAO.getMinTxnCommitTime();
        long maxTxnTime = txn.getCommitTimeMs();
        
        if (!last)
        {
            maxTxnTime = nodeDAO.getMaxTxnCommitTime();
        }
        
        long runEndTime = System.currentTimeMillis();
        
        boolean missing = inIndex.equals(InIndex.NO);
        
        IndexTxnInfo indexTxnInfo = new IndexTxnInfo(
                                            title, this.ipAddress,
                                            (missing ? 1 : 0), (missing ? txn : null), (missing ? txn : null),
                                            1, txn, txn,
                                            minTxnTime, maxTxnTime, runStartTime, runEndTime);
        
        logger.info(indexTxnInfo);
        
        if (! missing)
        {
            logger.info("Finished checking txn - txn id is in-sync on server "+this.ipAddress);
        }
        else
        {
            logger.warn("Finished checking txn - txn id is out-of-sync on server "+this.ipAddress);
        }
        
        lastReportRun = indexTxnInfo;
    
        return indexTxnInfo;
    }
	
	/* (non-Javadoc)
	 * @see org.alfresco.repo.admin.IndexCheckService#checkAllTxns()
	 */
	public IndexTxnInfo checkAllTxns()
    {
	    long fromTimeInclusive = 0L; // will be overridden to min time
	    long toTimeExclusive = 0L;   // will be overridden to max time
	    
	    String title = "Check all txns";
        return checkTxns(title, fromTimeInclusive, toTimeExclusive);
    }
	
	/* (non-Javadoc)
	 * @see org.alfresco.enterprise.repo.admin.IndexCheckService#checkTxnsFromTxn(long)
	 */
    public IndexTxnInfo checkTxnsFromTxn(long fromTxnId)
    {
        Transaction txn = nodeDAO.getTxnById(fromTxnId);
        
        // Shortcut
        if (txn == null)
        {
            logger.warn("Txn not found: "+fromTxnId);
            return null;
        }
        
        long fromTimeInclusive = txn.getCommitTimeMs();
        long toTimeExclusive = 0L;   // will be overridden to max time
        
        String title = "Check txns from (txnId="+fromTxnId+" at "+ISO8601DateFormat.format(new Date(fromTimeInclusive))+")";
        return checkTxns(title, fromTimeInclusive, toTimeExclusive);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.enterprise.repo.admin.IndexCheckService#checkTxnsFromToTxn(long, long)
     */
    public IndexTxnInfo checkTxnsFromToTxn(long fromTxnId, long toTxnId)
    {
        Transaction fromTxn = nodeDAO.getTxnById(fromTxnId);
        Transaction toTxn = nodeDAO.getTxnById(toTxnId);
        
        if (fromTxn == null)
        {
            logger.warn("From txn id not found: "+fromTxn);
            return null;
        }
        
        if (toTxn == null)
        {
            logger.warn("To txn id not found: "+toTxn);
            return null;
        }
        
        long fromTimeInclusive = fromTxn.getCommitTimeMs();
        long toTimeExclusive = toTxn.getCommitTimeMs()+1L;
        
        // note: this may check other txns that occurred at same time as toTxnId
        
        String title = "Check txns from (fromTxnId="+fromTxnId+" at "+ISO8601DateFormat.format(new Date(fromTimeInclusive))+") "+
                       "to (toTxnId="+toTxnId+" at "+ISO8601DateFormat.format(new Date(toTimeExclusive))+")";
        
        return checkTxns(title, fromTimeInclusive, toTimeExclusive);
    }
	
	/* (non-Javadoc)
	 * @see org.alfresco.enterprise.repo.admin.IndexCheckService#checkTxnsFromTime(java.util.Date)
	 */
	public IndexTxnInfo checkTxnsFromTime(Date fromDate)
    {
	    long fromTimeInclusive = fromDate.getTime();
	    long toTimeExclusive = 0L; // will be overridden to max time

	    String title = "Check txns from "+ISO8601DateFormat.format(new Date(fromTimeInclusive));
        return checkTxns(title, fromTimeInclusive, toTimeExclusive);
    }
       
	/* (non-Javadoc)
	 * @see org.alfresco.enterprise.repo.admin.IndexCheckService#checkTxnsFromToTime(java.util.Date, java.util.Date)
	 */
	public IndexTxnInfo checkTxnsFromToTime(Date fromDate, Date toDate)
    {
	    long fromTimeInclusive = fromDate.getTime();
        long toTimeExclusive = toDate.getTime()+1L;
        
        String title = "Check txns from "+ISO8601DateFormat.format(new Date(fromTimeInclusive))+" to "+ISO8601DateFormat.format(new Date(toTimeExclusive));
        return checkTxns(title, fromTimeInclusive, toTimeExclusive);
    }
	
    private IndexTxnInfo checkTxns(final String title, final long fromTimeInclusive, final long toTimeExclusive)
    {
        if (indexCheckLock.tryLock())
        {
            // started
            logger.info(title);
            
            try
            {
                RetryingTransactionCallback<IndexTxnInfo> indexCheckWork = new RetryingTransactionCallback<IndexTxnInfo>()
                {
                    public IndexTxnInfo execute() throws Exception
                    {
                        return checkTxnsImpl(title, fromTimeInclusive, toTimeExclusive);
                    }
                };
                
                return indexCheckWork.execute();
            }
            catch (Throwable e)
            {
                throw new AlfrescoRuntimeException("IndexCheck failure for " + this.getClass().getName(), e);
            }
            finally
            {
                try { indexCheckLock.unlock(); } catch (Throwable e) {}
                indexCheckStatusMsg = NO_INDEX_CHECK;
            }
        }
        else
        {
            logger.info("Bypassed IndexCheck work - already busy: " + this + "("+title+")");
        }
        
        long now = System.currentTimeMillis();
        
        IndexTxnInfo indexTxnInfo = new IndexTxnInfo("Bypass - already busy", this.ipAddress,
                                                     -1, null, null,
                                                     -1, null, null,
                                                     -1, -1, now, now);
        
        indexTxnInfo.setAlreadyRunning(true);       
        return indexTxnInfo;
    }
        
    private IndexTxnInfo checkTxnsImpl(String title, long fromTimeInclusive, long toTimeExclusive)
    {
        long runStartTime = System.currentTimeMillis();
        
	    long minTxnTime = nodeDAO.getMinTxnCommitTime();
	    long maxTxnTime = nodeDAO.getMaxTxnCommitTime();
	    
	    if (fromTimeInclusive == 0L)
	    {
	    	fromTimeInclusive = minTxnTime;
	    }
	    
	    if (toTimeExclusive == 0L)
	    {
	    	toTimeExclusive = maxTxnTime+1L;
	    }
	    
	    if (fromTimeInclusive > maxTxnTime)
	    {
	    	logger.warn("Nothing to check - fromTimeInclusive after maxTxnTime ("+fromTimeInclusive+" is after ("+maxTxnTime+")");
	    	return null;
	    }
	    
	    if (toTimeExclusive < minTxnTime)
	    {
	    	logger.warn("Nothing to check - toTimeExclusive before minTxnTime ("+toTimeExclusive+" is before ("+minTxnTime+")");
	    	return null;
	    }
	    
        if (fromTimeInclusive > toTimeExclusive)
        {
            logger.warn("Nothing to check - fromTimeInclusive after toTimeExclusive ("+fromTimeInclusive+" is after "+toTimeExclusive+")");
            return null;
        }
	    
	    logger.info("Start checking txns (from "+ISO8601DateFormat.format(new Date(fromTimeInclusive))+" to "+ISO8601DateFormat.format(new Date(toTimeExclusive))+")");

        long startTime = fromTimeInclusive;
        long processedTime = startTime;
        long diffTime = toTimeExclusive - fromTimeInclusive;
        
        List<Long> lastTxnIds = Collections.<Long>emptyList();
        
        int missingCount = 0;
        int processedCount = 0;
        
        Transaction firstMissingTxn = null;
        Transaction lastMissingTxn = null;
        
        Transaction firstProcessedTxn = null;
        Transaction lastProcessedTxn = null;
        
        while (true)
        {
            List<Transaction> nextTxns = nodeDAO.getTxnsByCommitTimeAscending(
                    fromTimeInclusive,
                    toTimeExclusive,
                    MAX_TRANSACTIONS_PER_ITERATION,
                    lastTxnIds,
                    false);

            indexCheckStatusMsg = String.format(
                    "Index checking batch of %d transactions from %s (txnId=%s)",
                    nextTxns.size(),
                    (new Date(fromTimeInclusive)).toString(),
                    nextTxns.isEmpty() ? "---" : nextTxns.get(0).getId().toString());
            
            lastTxnIds = new ArrayList<Long>(nextTxns.size());
            
            Iterator<Transaction> txnIterator = nextTxns.iterator();
            while (txnIterator.hasNext())
            {
                Transaction txn = txnIterator.next();
                Long txnId = txn.getId();
                
                if (txn.getCommitTimeMs() >= toTimeExclusive)
                {
                    // finished
                    nextTxns.clear();
                    break;
                }
                
                // Keep it to ensure we exclude it from the next iteration
                lastTxnIds.add(txnId);

                // As time passes while we are checking indexes and new changes may have been written through, we have
                // to start a new transaction for each transaction check
                InIndex inIndex = indexTransactionTracker.isTxnPresentInIndex(txn, true);
                
                // Although we use the same time as this transaction for the next iteration, we also
                // make use of the exclusion list to ensure that it doesn't get pulled back again.
                fromTimeInclusive = txn.getCommitTimeMs();
                
                if (firstProcessedTxn == null)
                {
                    firstProcessedTxn = txn;
                }
                
                lastProcessedTxn = txn;
                processedCount++;
                
                // dump a progress report every 10% of the way
                double before = (double) processedTime / (double) diffTime * 10.0;     // 0 - 10 
                processedTime = txn.getCommitTimeMs() - startTime;
                double after = (double) processedTime / (double) diffTime * 10.0;      // 0 - 10
                
                if (Math.floor(before) < Math.floor(after))                            // crossed a 0 - 10 integer boundary
                {
                    int complete = ((int)Math.floor(after))*10;
                    logger.info("Check progress: " + complete +"%");
                }
                
                if (inIndex.equals(InIndex.NO))
                {
                	if (firstMissingTxn == null)
                	{
                		firstMissingTxn = txn;
                	}
                	
                	lastMissingTxn = txn;
                	
                	missingCount++;
                }
            }
            
            // have we finished?
            if (nextTxns.size() == 0)
            {
                // there are no more
                break;
            }
        }
        
        long runEndTime = System.currentTimeMillis();
        
        IndexTxnInfo indexTxnInfo = new IndexTxnInfo(
                    title,
                    getIpAddress(),
                    missingCount,
                    firstMissingTxn,
                    lastMissingTxn,
                    processedCount,
                    firstProcessedTxn,
                    lastProcessedTxn,
                    minTxnTime, 
                    maxTxnTime,
                    runStartTime,
                    runEndTime);
        
        logger.info(indexTxnInfo);
        
        if (missingCount == 0)
        {
        	logger.info("Finished checking txns - no out-of-sync txn ids found (processed "+processedCount+" txn ids) on server "+this.ipAddress);
        }
        else
        {
        	logger.warn("Finished checking txns - found "+missingCount+" out-of-sync txn ids (processed "+processedCount+" txn ids) on server "+this.ipAddress);
        }
        
        lastReportRun = indexTxnInfo;
         
        return indexTxnInfo;
    }
	
    public IndexTxnInfo getStatusForTxnNodes(long txnId)
    {
        long runStartTime = System.currentTimeMillis();
        
        String title = "Check status of txn nodes (txnId="+txnId+")";
        logger.info(title);
        
        List<NodeRef.Status> nodeStatuses = nodeDAO.getTxnChanges(txnId);
        
        List<IndexNodeInfo> indexNodeInfos = new ArrayList<IndexNodeInfo>(nodeStatuses.size());
        
        for (NodeRef.Status nodeStatus : nodeStatuses)
        {
            indexNodeInfos.add(getNodeIndexInfo(nodeStatus));
        }
        
        Transaction txn = nodeDAO.getTxnById(txnId);
        InIndex inIndex = indexTransactionTracker.isTxnPresentInIndex(txn); // note: in case of null txn, returns InIndex.YES
        
        long runEndTime = System.currentTimeMillis();
        
        boolean missing = inIndex.equals(InIndex.NO);
        
        IndexTxnInfo indexTxnInfo = new IndexTxnInfo(
                title,
                getIpAddress(),
                (missing ? 1 : 0), (missing ? txn : null), (missing ? txn : null),
                1, txn, txn,
                -1, 
                -1,
                runStartTime,
                runEndTime);

        indexTxnInfo.setNodeList(indexNodeInfos);
        
        lastReportRun = indexTxnInfo;
        
        return indexTxnInfo;
    }
	
    public IndexTxnInfo getStatusForNode(NodeRef nodeRef)
    {
        long runStartTime = System.currentTimeMillis();
        
        String title = "Check status of node (nodeRef="+nodeRef+")";
        logger.info(title);
        
        Status nodeStatus = nodeDAO.getNodeRefStatus(nodeRef);
        if (nodeStatus == null)
        {
            throw new AlfrescoRuntimeException("Node status not found for '" + nodeRef+ "'.");
        }
        // Check if it is in the index or not
        IndexNodeInfo indexNodeInfo = getNodeIndexInfo(nodeStatus);
        
        List<IndexNodeInfo> indexNodeInfos = new ArrayList<IndexNodeInfo>(1);
        indexNodeInfos.add(indexNodeInfo);
        
        if (indexNodeInfo.getNodeStatus() == null)
        {
            throw new AlfrescoRuntimeException("Node status not found for '"+nodeRef+"'");
        }
        
        Long txnId = indexNodeInfo.getNodeStatus().getDbTxnId();
        Transaction txn = null;
        if (txnId != null)
        {
            txn = nodeDAO.getTxnById(txnId);
        }
        
        InIndex inIndex = indexTransactionTracker.isTxnPresentInIndex(txn); // note: in case of null txn, returns InIndex.YES
        
        long runEndTime = System.currentTimeMillis();
        
        boolean missing = inIndex.equals(InIndex.NO);
        
        IndexTxnInfo indexTxnInfo = new IndexTxnInfo(
                title,
                getIpAddress(),
                (missing ? 1 : 0), (missing ? txn : null), (missing ? txn : null),
                1, txn, txn,
                -1, 
                -1,
                runStartTime,
                runEndTime);
        
        indexTxnInfo.setNodeList(indexNodeInfos);
        
        lastReportRun = indexTxnInfo;
        
        return indexTxnInfo;
    }
    
    private IndexNodeInfo getNodeIndexInfo(NodeRef.Status nodeStatus)
    {
        ParameterCheck.mandatory("nodeStatus", nodeStatus);
        
        InIndex inIndex = isNodeRefPresentInIndex(nodeStatus.getNodeRef());
        
        return new IndexNodeInfo(nodeStatus, inIndex);
    }
    
    private InIndex isNodeRefPresentInIndex(final NodeRef nodeRef)
    {
        ParameterCheck.mandatory("nodeRef", nodeRef);
        
        StoreRef storeRef = nodeRef.getStoreRef();
        
        if (indexTransactionTracker.isIgnorableStore(storeRef))
        {
            return InIndex.YES;
        }
        
        if (logger.isDebugEnabled())
        {
            logger.debug("Checking for nodeRef in index: " + nodeRef);
        }
        
        // Check if the txn ID is present in any store's index
        boolean foundInIndex = false;
        
        ResultSet results = null;
        try
        {
            SearchParameters sp = new SearchParameters();
            sp.addStore(storeRef);
            
            // search for it in the index, sorting with youngest first, fetching only 1
            sp.setLanguage(SearchService.LANGUAGE_LUCENE);
            sp.setQuery("ID:" + SearchLanguageConversion.escapeLuceneQuery(nodeRef.toString()));
            sp.setLimit(1);
            
            results = searcher.query(sp);
            
            if (results.length() > 0)
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug("Index has results for nodeRef " + nodeRef + " for store " + storeRef);
                }
                foundInIndex = true; // there were updates/creates and results for the nodeRef were found
            }
            else
            {
                if (logger.isTraceEnabled())
                {
                    logger.trace("NodeRef " + nodeRef + " not in index for store " + storeRef + ".  Possibly out of date.");
                }
                foundInIndex = false;
            }
        }
        finally
        {
            if (results != null) { results.close(); }
        }
        
        InIndex result = InIndex.NO;
        if (foundInIndex)
        {
            result = InIndex.YES;
        }
        
        // done
        if (logger.isDebugEnabled())
        {
            logger.debug("NodeRef " + nodeRef + " present in indexes: " + result);
        }
        return result;
    }

    public Thread reindexFromTxn(long txnId)
    {
        logger.info("Reset index tracker to re-index from txn id: "+txnId);
        
        // reset index transaction tracker - to reindex from a given transaction id
        indexTransactionTracker.resetFromTxn(txnId);
        
        // create daemon thread to re-index in the background since by default,
        //    index transaction tracker is configured not to run.
        // Note: it is OK if index transaction tracker is configured to execute,
        //    eg. in a cluster (will drop through if already running)
        
        Runnable runnable = new ReindexRunner();
        Thread thread = new Thread(runnable);
        thread.setName("ReindexRunner");
        thread.setDaemon(true);
        // start it
        thread.start();
        return thread;
    }
    
    public Thread reindexFromTime(Date fromTime)
    {
        long fromTimeInclusive = fromTime.getTime();
        long minTxnTime = nodeDAO.getMinTxnCommitTime();
        
        if (fromTimeInclusive < minTxnTime)
        {
            logger.warn("From time "+ISO8601DateFormat.format(new Date(fromTimeInclusive))+" set to min txn time "+ISO8601DateFormat.format(new Date(minTxnTime)));
            fromTimeInclusive = minTxnTime;
        }
        
        Long maxTxnId = nodeDAO.getMaxTxnIdByCommitTime(fromTimeInclusive);
        
        // Shortcut
        if (maxTxnId == null)
        {
            logger.warn("No txns found: "+fromTimeInclusive);
            return null;
        }
        
        return reindexFromTxn(maxTxnId);
    }
    
    public String getReindexProgress()
    {
        return indexTransactionTracker.getReindexStatus();
    }
    
    public String getIndexCheckProgress()
    {
        return indexCheckStatusMsg;
    }
    
    private class ReindexRunner implements Runnable
    {
        public void run()
        {
            try
            {
                // note: if index tracker is running already then will return immediately else will run inline ...
                indexTransactionTracker.reindex();
            }
            catch (Throwable e)
            {
                // report
                logger.error("Re-index failure", e);
            }
        }
    }
    
    public static void main(String[] args)
    {
        ApplicationContext ctx = ApplicationContextHelper.getApplicationContext();
        final IndexCheckService admIndexCheckService = (IndexCheckService) ctx.getBean("search.admIndexCheckService");
        RetryingTransactionHelper txnHelper = (RetryingTransactionHelper) ctx.getBean("retryingTransactionHelper");
        System.out.println("Checking index....");
        txnHelper.doInTransaction(new RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                IndexTxnInfo info = admIndexCheckService.checkAllTxns();
                System.out.println(info);
                return null;
            }
        });
    }
}
