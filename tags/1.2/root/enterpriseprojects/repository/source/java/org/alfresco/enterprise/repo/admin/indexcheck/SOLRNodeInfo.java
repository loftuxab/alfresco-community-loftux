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
import javax.management.openmbean.TabularType;

import org.apache.solr.common.util.NamedList;

/**
 * Conveys Alfresco node information encoded in a Solr index
 * 
 * @since 4.0
 *
 */
public class SOLRNodeInfo
{
	private static String[] keyAttributes = {"Node DBID"};
	private static String[] attributeKeys = {"Node DBID", "DB TX", "DB TX status", "Leaf doc in Index", "Aux doc in Index", "Leaf tx in Index", "Aux tx in Index"};
	private static String[] attributeDescriptions = {"Node DBID", "DB TX", "DB TX status", "Leaf doc in Index", "Aux doc in Index", "Leaf tx in Index", "Aux tx in Index"};
	private static OpenType<?>[] attributeTypes = {SimpleType.LONG, SimpleType.LONG, SimpleType.STRING, SimpleType.LONG, SimpleType.LONG, SimpleType.LONG, SimpleType.LONG};
	
	private static CompositeType compositeType;
	private static TabularType tabularType;

	private String nodeName;
	private Map<String, Object> values;

	public static CompositeType getCompositeType() throws OpenDataException
	{
		if(compositeType == null)
		{
			compositeType = new CompositeType("SOLR Acls", "SOLR Acls", attributeKeys, attributeDescriptions, attributeTypes);
		}
		return compositeType;
	}

	public static TabularType getTabularType() throws OpenDataException
	{
		if(tabularType == null)
		{
			tabularType = new TabularType("SOLR Acls", "SOLR Acls", SOLRNodeInfo.getCompositeType(), keyAttributes);
		}
		return tabularType;
	}
	
	public SOLRNodeInfo(NamedList<Object> node)
	{
		int size = node.size();
		values = new HashMap<String, Object>(size);
		for(String title : attributeKeys)
		{
			Object value = node.get(title);
			values.put(title, value);
		}
	}

	public Map<String, Object> getValues()
	{
		return values;
	}
	
	public CompositeData getCompositeData() throws OpenDataException
	{
		CompositeType compositeType = SOLRNodeInfo.getCompositeType();
		return new CompositeDataSupport(compositeType, getValues());
	}
	
	public String getNodeName()
	{
		return nodeName;
	}

	public void setNodeName(String nodeName)
	{
		this.nodeName = nodeName;
	}

}
