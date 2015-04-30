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

import java.util.Date;

import org.apache.solr.common.util.NamedList;

/**
 * Metadata pertaining to a Solr index core.
 * 
 * @since 4.0
 */
public class SOLRIndexInfo
{
	private String coreName;
	private NamedList<Object> coreInfo;
	private NamedList<Object> indexInfo;
	private long timestamp;

	public SOLRIndexInfo(String coreName, NamedList<Object> coreInfo)
	{
		this.coreName = coreName;
		this.coreInfo = coreInfo;
		this.indexInfo = (NamedList<Object>)coreInfo.get("index");
		this.timestamp = System.currentTimeMillis();
	}
	
	public long getTimestamp()
	{
		return timestamp;
	}

	public String getInstanceDirectory()
	{
		return (String)coreInfo.get("instanceDir");
	}
	
	public String getDataDirectory()
	{
		return (String)coreInfo.get("dataDir");
	}
	
	public Date getStartTime()
	{
		return (Date)coreInfo.get("startTime");
	}
	
	public Long getUptime()
	{
		return (Long)coreInfo.get("uptime");
	}
	
	public Integer getNumDocuments()
	{
		return (Integer)indexInfo.get("numDocs");
	}
	
	public Integer getMaxDocument()
	{
		return (Integer)indexInfo.get("maxDoc");
	}

	public Long getVersion()
	{
		return (Long)indexInfo.get("version");
	}
	
	public Boolean getOptimized()
	{
		return (Boolean)indexInfo.get("optimized");
	}
	
	public Boolean getCurrent()
	{
		return (Boolean)indexInfo.get("current");
	}
	
	public Boolean getHasDeletions()
	{
		return (Boolean)indexInfo.get("hasDeletions");
	}
	
	public String getIndexInstanceDirectory()
	{
		return (String)indexInfo.get("directory");
	}

	public Date getLastModified()
	{
		return (Date)indexInfo.get("lastModified");
	}
}
