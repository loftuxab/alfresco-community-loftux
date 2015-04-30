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
 * Conveys information about Alfresco transaction acls stored in a SOLR index
 * 
 * @since 4.0
 *
 * TODO deal with lots of acls - how to present in jmx clients?
 */
public class SOLRAclTransactionInfo
{
	private String core;

	private SOLRTransactionReport txnReport;
	private List<SOLRAclInfo> acls;
	
	public SOLRAclTransactionInfo(String core, NamedList<Object> txn, long txnId)
	{
		this.core = core;

		acls = new ArrayList<SOLRAclInfo>();
		
		SOLRTransactionReport txnReport = SOLRTransactionReport.getReport(core, (NamedList<Object>)txn.get("transaction"));
		setTransactionReport(txnReport);

	    NamedList<Object> nodes = (NamedList<Object>)txn.get("nodes");
		if(nodes != null)
		{
			for(java.util.Map.Entry<String, Object> node : nodes)
			{
				NamedList<Object> aclDetails = (NamedList<Object>)node.getValue();

				SOLRAclInfo aclInfo = new SOLRAclInfo(aclDetails);
				addAcl(aclInfo);
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
	
	public void addAcl(SOLRAclInfo aclInfo)
	{
		acls.add(aclInfo);
	}

	public List<SOLRAclInfo> getAcls()
	{
		return acls;
	}

	public TabularData getAclsTabularData() throws OpenDataException
	{
		CompositeType compositeType = acls.get(0).getCompositeType();
		TabularType tType = acls.get(0).getTabularType();
		TabularDataSupport table = new TabularDataSupport(tType);
		for(SOLRAclInfo acl : acls)
		{
			CompositeDataSupport row = new CompositeDataSupport(compositeType, acl.getValues());
			table.put(row);
		}

		return table;
	}

}
