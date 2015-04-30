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
import java.util.List;

import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.TabularData;
import javax.management.openmbean.TabularDataSupport;
import javax.management.openmbean.TabularType;

import org.apache.solr.common.util.NamedList;

/**
 * Conveys information about Alfresco transaction nodes stored in a SOLR index
 * 
 * @since 4.0
 *
 */
public class SOLRTransactionNodeInfo
{
	private SOLRTransactionReport txnReport;
	private List<SOLRNodeInfo> nodes;
	
	public SOLRTransactionNodeInfo(String core, NamedList<Object> txn)
	{
		nodes = new ArrayList<SOLRNodeInfo>();

		SOLRTransactionReport txnReport = SOLRTransactionReport.getReport(core, (NamedList<Object>)txn.get("transaction"));
		setTransactionReport(txnReport);

	    NamedList<Object> nodes = (NamedList<Object>)txn.get("nodes");
		if(nodes != null)
		{
			for(java.util.Map.Entry<String, Object> node : nodes)
			{
				NamedList<Object> nodeDetails = (NamedList<Object>)node.getValue();

				SOLRNodeInfo nodeInfo = new SOLRNodeInfo(nodeDetails);
				addNode(nodeInfo);
			}
		}
	}
	
	public void setTransactionReport(SOLRTransactionReport report)
	{
		this.txnReport = report;
	}
	
	public SOLRTransactionReport getTransactionReport()
	{
		return this.txnReport;
	}
	
	public void addNode(SOLRNodeInfo nodeInfo)
	{
		nodes.add(nodeInfo);
	}
	
	public List<SOLRNodeInfo> getNodes()
	{
		return nodes;
	}
	
	public TabularData getNodesTabularData() throws OpenDataException
	{
		CompositeType compositeType = SOLRNodeInfo.getCompositeType();
		TabularType tType = SOLRNodeInfo.getTabularType();
		TabularDataSupport table = new TabularDataSupport(tType);

		for(SOLRNodeInfo node : nodes)
		{
			CompositeDataSupport row = new CompositeDataSupport(compositeType, node.getValues());
			table.put(row);
		}

		return table;
	}
	
	public static SOLRTransactionNodeInfo getSOLRTransactionInfo(String core, NamedList<Object> txn, long txnId)
	{
		SOLRTransactionNodeInfo info = new SOLRTransactionNodeInfo(core, txn);
	    return info;
	}
}
