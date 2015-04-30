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

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;
import javax.management.openmbean.TabularType;

import org.apache.solr.common.util.NamedList;

/**
 * Conveys information about Alfresco transaction acl stored in a SOLR index
 * 
 * @since 4.0
 */
public class SOLRAclInfo
{
	private static String[] attributeKeys = {"Acl Id", "Acl doc in index", "Acl tx in Index"};
	private static String[] attributeDescriptions = {"Acl Id", "Acl doc in index", "Acl tx in Index"};
	private static OpenType<?>[] attributeTypes = {SimpleType.LONG, SimpleType.LONG, SimpleType.LONG};

	private static CompositeType compositeType;
	private static TabularType tabularType;

	private String aclName;

	private Map<String, Object> values;
	
	public SOLRAclInfo(NamedList<Object> acl)
	{
		int size = acl.size();
		values = new HashMap<String, Object>(size);
		for(String title : attributeKeys)
		{
			Object value = acl.get(title);
			values.put(title, value);
		}
		
	    long aclId = ((Long)acl.get("Acl Id")).longValue();
		setAclName("Acl Id" + aclId);
	}

	public Map<String, Object> getValues()
	{
		return values;
	}
	
	public CompositeData getCompositeData() throws OpenDataException
	{
		CompositeType compositeType = getCompositeType();
		return new CompositeDataSupport(compositeType, getValues());
	}

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
			tabularType = new TabularType("SOLR Acls", "SOLR Acls", getCompositeType(), attributeKeys);
		}
		return tabularType;
	}

	public AttributeList getAttributeList()
	{
		AttributeList list = new AttributeList();
    	for(String title : attributeKeys)
    	{
    		list.add(new Attribute(title, values.get(title)));    		
    	}

		return list;
	}
	
	public String getAclName()
	{
		return aclName;
	}

	public void setAclName(String aclName)
	{
		this.aclName = aclName;
	}

}
