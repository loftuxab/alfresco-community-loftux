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

import java.util.HashMap;
import java.util.Map;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;

import org.alfresco.util.JMXUtils;
import org.apache.solr.common.util.NamedList;

/**
 * Conveys information about a sequence of Alfresco transactions stored in a SOLR index
 * 
 * @since 4.0
 *
 */
public class SOLRTransactionReport
{
	private String core;
	private String[] attributeNames;
	private Map<String, Object> values;
	private OpenType<?>[] types;
	
	public SOLRTransactionReport(String core, NamedList<Object> report)
	{
		int size = report.size() + 1; // for the core name, generated here
		attributeNames = new String[size];
		values = new HashMap<String, Object>(size);
		types = new OpenType<?>[size];
		
		attributeNames[0] = "Core Name";
		values.put("Core Name", core);
		types[0] = SimpleType.STRING;
		
		for(int i = 1; i < size; i++)
		{
			String name = report.getName(i-1);
			Object value = report.getVal(i-1);
			attributeNames[i] = name;
			values.put(name, value);
			types[i] = JMXUtils.getOpenType(value);
		}
	}

	String[] getAttributeNames()
	{
		return attributeNames;
	}

	Map<String, Object> getValues()
	{
		return values;
	}

	OpenType<?>[] getTypes()
	{
		return types;
	}
	
	public CompositeType getCompositeType() throws OpenDataException
	{
		return new CompositeType("Solr Transaction Report", "Solr Transaction Report",
				attributeNames, attributeNames, types);
	}
	
	public CompositeData getCompositeData() throws OpenDataException
	{
        CompositeType xct = getCompositeType();
        CompositeDataSupport data = new CompositeDataSupport(xct, getValues());
        return data;
	}
	
	public static SOLRTransactionReport getReport(String core, NamedList<Object> report)
	{
		SOLRTransactionReport txnReport = new SOLRTransactionReport(core, report);
	    return txnReport;
	}

}
