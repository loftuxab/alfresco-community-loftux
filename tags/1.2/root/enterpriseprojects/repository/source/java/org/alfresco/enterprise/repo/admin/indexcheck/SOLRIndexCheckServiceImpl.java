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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.alfresco.enterprise.repo.management.SOLRIndex;
import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.repo.solr.SOLRAdminClient;
import org.alfresco.util.ISO8601DateFormat;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.common.util.NamedList;

/**
 * This class calls the SOLR admin urls to provide information about the SOLR indexes
 * 
 * @since 4.0
 *
 */
public class SOLRIndexCheckServiceImpl implements SOLRIndexCheckService
{
	private static final Log logger = LogFactory.getLog(SOLRIndex.class);
	
	private SOLRAdminClient client;

	public SOLRIndexCheckServiceImpl()
	{
	}
	
	public void setSolrAdminClient(SOLRAdminClient client)
	{
		this.client = client;
	}
    
	/**
     * {@inheritDoc}
     */
    @Override
	public List<String> getRegisteredCores()
	{
    	return client.getRegisteredCores();
	}
    
    /**
     * {@inheritDoc}
     */
    @Override
    public SOLRIndexInfo indexInfo(String core)
    {
    	try
    	{
		    ModifiableSolrParams params = new ModifiableSolrParams();
		    params.set("qt", "/admin/cores");
		    params.set("action", "STATUS");
		    params.set("core", core);
			
		    QueryResponse response = client.query(params);
		    
			if(logger.isDebugEnabled())
			{
				logger.debug("solr response status = " + response.getStatus());
			}
			
		    NamedList<Object> results = response.getResponse();
		    NamedList<Object> status = (NamedList<Object>)results.get("status");
		    NamedList<Object> coreInfo = (NamedList<Object>)status.get(core);
		    SOLRIndexInfo indexInfo = new SOLRIndexInfo(core, coreInfo);
		    return indexInfo;
		}
		catch(SolrServerException e)
		{
			throw new AlfrescoRuntimeException("", e);
		}
    }

    /**
     * {@inheritDoc}
     */
    @Override
	public SOLRTransactionReport transactionReport(String core, long fromTxnId, long toTxnId, long fromAclTxnId, long toAclTxnId,
			String fromTimeISO8601, String toTimeISO8601)
	{
		try
		{
			Date fromTime = null;
			try
			{
				fromTime = (fromTimeISO8601 != null && !fromTimeISO8601.equals("") && !fromTimeISO8601.equals("0") ? ISO8601DateFormat.parse(fromTimeISO8601) : null);
			}
			catch(AlfrescoRuntimeException e)
			{
				fromTime = null;
			}

			Date toTime = null;
			try
			{
				toTime = (toTimeISO8601 != null  && !toTimeISO8601.equals("") && !toTimeISO8601.equals("0") ? ISO8601DateFormat.parse(toTimeISO8601) : null);
			}
			catch(AlfrescoRuntimeException e)
			{
				toTime = null;
			}
		
		    ModifiableSolrParams params = new ModifiableSolrParams();
		    params.set("qt", "/admin/cores");
		    params.set("action", "REPORT");
		    //params.set("core", core);
		    if(fromTxnId > 0)
		    {
			    params.set("fromTx", String.valueOf(fromTxnId));		    	
		    }
		    if(toTxnId > 0)
		    {
		    	params.set("toTx", String.valueOf(toTxnId));
		    }
		    if(fromTime != null)
		    {
			    long fromTimeInclusive = fromTime.getTime();
		    	params.set("fromTime", String.valueOf(fromTimeInclusive));
		    }
		    if(toTime != null)
		    {
			    long toTimeInclusive = toTime.getTime();
		    	params.set("toTime", String.valueOf(toTimeInclusive));
		    }
		    if(fromAclTxnId > 0)
		    {
		    	params.set("fromAclTx", String.valueOf(fromAclTxnId));
		    }
		    if(toAclTxnId > 0)
		    {
		    	params.set("toAclTx", String.valueOf(toAclTxnId));
		    }
		    
		    QueryResponse response = client.query(params);
		    
			if(logger.isDebugEnabled())
			{
				logger.debug("solr response status = " + response.getStatus());
			}

		    NamedList<Object> results = response.getResponse();
		    NamedList<Object> report = (NamedList<Object>)results.get("report");
		    NamedList<Object> coreInfo = (NamedList<Object>)report.get(core);
		    
		    SOLRTransactionReport txnReport = SOLRTransactionReport.getReport(core, coreInfo);
		    return txnReport;
		}
		catch(SolrServerException e)
		{
			throw new AlfrescoRuntimeException("", e);
		}
	}

    /**
     * {@inheritDoc}
     * 
     * TODO deal with lots of nodes e.g. limit by using offset, limit paging
     */
    @Override
	public SOLRTransactionNodeInfo transactionNodesReport(String core, long txnId)
	{
		try
		{
		    ModifiableSolrParams params = new ModifiableSolrParams();
		    params.set("qt", "/admin/cores");
		    params.set("action", "TXREPORT");
		    params.set("core", core);

		    if(txnId < 1)
		    {
		    	throw new IllegalArgumentException("Transaction id must be greater than 0");
		    }
	
		    params.set("txid", String.valueOf(txnId));		    	
		    
		    QueryResponse response = client.query(params);
	
		    logger.debug("solr response status = " + response.getStatus());
		    NamedList<Object> results = response.getResponse();
		    NamedList<Object> report = (NamedList<Object>)results.get("report");
		    NamedList<Object> coreInfo = (NamedList<Object>)report.get(core);

		    SOLRTransactionNodeInfo txnInfo = SOLRTransactionNodeInfo.getSOLRTransactionInfo(core, coreInfo, txnId);
		    return txnInfo;
		}
		catch(SolrServerException e)
		{
			throw new AlfrescoRuntimeException("", e);
		}
	}

    /**
     * {@inheritDoc}
     * 
     * TODO deal with lots of nodes e.g. limit by using offset, limit paging
     */
    @Override
	public SOLRAclTransactionInfo aclTransactionReport(String core, long txnId)
	{
		try
		{
		    ModifiableSolrParams params = new ModifiableSolrParams();
		    params.set("qt", "/admin/cores");
		    params.set("action", "ACLTXREPORT");
		    params.set("core", core);
		    
		    if(txnId < 1)
		    {
		    	throw new IllegalArgumentException("Transaction id must be greater than 0");
		    }
	
		    params.set("acltxid", String.valueOf(txnId));		    	
			
		    QueryResponse response = client.query(params);

		    logger.debug("solr response status = " + response.getStatus());
		    NamedList<Object> results = response.getResponse();
		    NamedList<Object> report = (NamedList<Object>)results.get("report");
		    NamedList<Object> coreInfo = (NamedList<Object>)report.get(core);

		    SOLRAclTransactionInfo txnInfo = new SOLRAclTransactionInfo(core, coreInfo, txnId);
		    

			
		    return txnInfo;
		}
		catch(SolrServerException e)
		{
			throw new AlfrescoRuntimeException("", e);
		}
	}

    /**
     * {@inheritDoc}
     */
    @Override
	public SOLRNodeInfo nodeReport(String core, long dbid)
	{
		try
		{
		    ModifiableSolrParams params = new ModifiableSolrParams();
		    params.set("qt", "/admin/cores");
		    params.set("action", "NODEREPORT");
		    params.set("core", core);
		    
		    if(dbid < 1)
		    {
		    	throw new IllegalArgumentException("dbid must be greater than 0");
		    }
	
		    params.set("dbid", String.valueOf(dbid));		    	
		    
		    QueryResponse response = client.query(params);
	
		    logger.debug("solr response status = " + response.getStatus());

		    NamedList<Object> results = response.getResponse();
		    NamedList<Object> report = (NamedList<Object>)results.get("report");
		    NamedList<Object> coreInfo = (NamedList<Object>)report.get(core);

			SOLRNodeInfo info = null;
			if(coreInfo != null)
			{
				info = new SOLRNodeInfo(coreInfo);
			}
			
			return info;
		}
		catch(SolrServerException e)
		{
			throw new AlfrescoRuntimeException("", e);
		}
	}

    /**
     * {@inheritDoc}
     */
    @Override
	public SOLRAclInfo aclReport(String core, long aclId)
	{
		try
		{
		    ModifiableSolrParams params = new ModifiableSolrParams();
		    params.set("qt", "/admin/cores");
		    params.set("action", "ACLREPORT");
		    params.set("core", core);
		    
		    if(aclId < 1)
		    {
		    	throw new IllegalArgumentException("aclId must be greater than 0");
		    }
	
		    params.set("aclid", String.valueOf(aclId));		    	
		    
		    QueryResponse response = client.query(params);
	
		    logger.debug("solr response status = " + response.getStatus());

		    NamedList<Object> results = response.getResponse();
		    NamedList<Object> report = (NamedList<Object>)results.get("report");
		    NamedList<Object> coreInfo = (NamedList<Object>)report.get(core);

		    SOLRAclInfo info = null;
			if(coreInfo != null)
			{
				info = new SOLRAclInfo(coreInfo);
			}
			
			return info;
		}
		catch(SolrServerException e)
		{
			throw new AlfrescoRuntimeException("", e);
		}
	}

	protected List<SOLRAclInfo> getAcls(NamedList<Object> input)
	{
		List<SOLRAclInfo> acls = new ArrayList<SOLRAclInfo>();

	    NamedList<Object> nodes = (NamedList<Object>)input.get("nodes");
		if(nodes != null)
		{
			for(java.util.Map.Entry<String, Object> node : nodes)
			{
				NamedList<Object> aclDetails = (NamedList<Object>)node.getValue();

				SOLRAclInfo aclInfo = new SOLRAclInfo(aclDetails);
				acls.add(aclInfo);
			}
		}
		
		return acls;
	}

    /* (non-Javadoc)
     * @see org.alfresco.enterprise.repo.admin.indexcheck.SOLRIndexCheckService#fix()
     */
    @Override
    public void checkAndFixIndex(String core)
    {
        try
        {
            ModifiableSolrParams params = new ModifiableSolrParams();
            params.set("qt", "/admin/cores");
            params.set("action", "FIX"); 
            params.set("core", core);
            
            QueryResponse response = client.query(params);
    
            logger.debug("solr response status = " + response.getStatus());

        }
        catch(SolrServerException e)
        {
            throw new AlfrescoRuntimeException("", e);
        }
        
    }

    /* (non-Javadoc)
     * @see org.alfresco.enterprise.repo.admin.indexcheck.SOLRIndexCheckService#backUpIndex(java.lang.String, java.lang.String)
     */
    @Override
    public void backUpIndex(String core, String remoteLocation)
    {
        try
        {
            ModifiableSolrParams params = new ModifiableSolrParams();
            params.set("qt", "/"+core+"/replication");
            params.set("command", "backup"); 
            params.set("location", remoteLocation);
            
            QueryResponse response = client.query(params);
    
            logger.debug("solr response status = " + response.getStatus());

        }
        catch(SolrServerException e)
        {
            throw new AlfrescoRuntimeException("", e);
        }
        
    }

    /* (non-Javadoc)
     * @see org.alfresco.enterprise.repo.admin.indexcheck.SOLRIndexCheckService#rebuildIndexCache(java.lang.String)
     */
    @Override
    public void rebuildIndexCache(String core)
    {
        try
        {
            ModifiableSolrParams params = new ModifiableSolrParams();
            params.set("qt", "/admin/cores");
            params.set("action", "CHECK"); 
            params.set("core", core);
            
            QueryResponse response = client.query(params);
    
            logger.debug("solr response status = " + response.getStatus());

        }
        catch(SolrServerException e)
        {
            throw new AlfrescoRuntimeException("", e);
        }
        
    }

}
