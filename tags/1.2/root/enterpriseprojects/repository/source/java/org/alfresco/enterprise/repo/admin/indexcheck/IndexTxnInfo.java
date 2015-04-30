/*
 * Copyright 2005-2010 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.admin.indexcheck;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.alfresco.repo.domain.node.Transaction;
import org.springframework.extensions.surf.util.ISO8601DateFormat;


/**
 * Index txn info
 *
 * @author janv
 */
public class IndexTxnInfo implements Serializable
{
    private static final long serialVersionUID = 7854749986083635678L;
    
    private String title;
    private String ipAddress; // for this app server
    
    private int missingCount;
    private Transaction firstMissingTxn;
    private Transaction lastMissingTxn;
    
    private int processedCount;
    private Transaction firstProcessedTxn;
    private Transaction lastProcessedTxn;
    
    private Date minTxnTime;
    private Date maxTxnTime;
    
    private Date runStartTime;
    private Date runEndTime;
    
    private boolean alreadyRunning = false;
    
    private List<IndexNodeInfo> nodeList;
    
    
    /* package */ IndexTxnInfo(String title, String ipAddress, 
                               int missingCount, Transaction firstMissing, Transaction lastMissing, 
                               int processedCount, Transaction firstProcessed, Transaction lastProcessed,
                               long minTxnTime, long maxTxnTime, long runStartTime, long runEndTime)
    {
        this.title = title;
        this.ipAddress = ipAddress;
        
        this.missingCount = missingCount;
        this.firstMissingTxn = firstMissing;
        this.lastMissingTxn = lastMissing;
        
        this.processedCount = processedCount;
        this.firstProcessedTxn = firstProcessed;
        this.lastProcessedTxn = lastProcessed;
        
        this.minTxnTime = new Date(minTxnTime);
        this.maxTxnTime = new Date(maxTxnTime);
        
        this.runStartTime = new Date(runStartTime);
        this.runEndTime = new Date(runEndTime);
    }
    
    public Transaction getFirstMissingTxn()
    {
        return this.firstMissingTxn;
    }
    
    public Transaction getLastMissingTxn()
    {
        return this.lastMissingTxn;
    }
    
    public Transaction getFirstProcessedTxn()
    {
        return this.firstProcessedTxn;
    }
    
    public Transaction getLastProcessedTxn()
    {
        return this.lastProcessedTxn;
    }
    
    public String getTitle()
    {
        return this.title;
    }
    
    /**
     * Which server was this check run on ?
     * 
     * @return
     */
    public String getIpAddress()
    {
        return this.ipAddress;
    }

    /**
     * Get missing count of txn ids (for processed time range) or 0
     * 
     * ie. are any DB txn ids missing from index (ie. across stores) for this app server
     * 
     * @return
     */
    public int getMissingCount() 
    {
		return missingCount;
	}

    /**
     * Get processed count of txn ids (for processed time range)
     * 
     * @return
     */
	public int getProcessedCount()
	{
		return processedCount;
	}

	public Date getMinTxnTime()
	{
		return minTxnTime;
	}

	public Date getMaxTxnTime()
	{
		return maxTxnTime;
	}
	
	public Date getRunStartTime()
	{
		return runStartTime;
	}

	public Date getRunEndTime()
	{
		return runEndTime;
	}
	
    public boolean isAlreadyRunning()
    {
        return alreadyRunning;
    }

    public void setAlreadyRunning(boolean alreadyRunning)
    {
        this.alreadyRunning = alreadyRunning;
    }
    
    public List<IndexNodeInfo> getNodeList()
    {
        return nodeList;
    }

    public void setNodeList(List<IndexNodeInfo> nodeList)
    {
        this.nodeList = nodeList;
    }

	
	// debug display string
    public String toString()
    {
        StringBuilder sb = new StringBuilder(50);
        
        sb.append("IndexTxnInfo[")
          .append("\n\trunStartTime=")
          .append(ISO8601DateFormat.format(runStartTime))
          .append("\n\trunEndTime=")
          .append(ISO8601DateFormat.format(runEndTime))
          .append("\n\tmissingCount=")
          .append(missingCount)
          .append("\n\tfirstMissingTxn=")
          .append(firstMissingTxn)
          .append("\n\tlastMissingTxn=")
          .append(lastMissingTxn)
          .append("\n\tprocessedCount=")
          .append(processedCount)
          .append("\n\tfirstProcessedTxn=")
          .append(firstProcessedTxn)
          .append("\n\tlastProcessedTxn=")
          .append(lastProcessedTxn)
          .append("\n\tserver=")
          .append(ipAddress)
          .append("\n\tminTxnTime=")
          .append(ISO8601DateFormat.format(minTxnTime))
          .append("\n\tmaxTxnTime=")
          .append(ISO8601DateFormat.format(maxTxnTime))
          .append("\n]");
        
        return sb.toString();
    }
}
