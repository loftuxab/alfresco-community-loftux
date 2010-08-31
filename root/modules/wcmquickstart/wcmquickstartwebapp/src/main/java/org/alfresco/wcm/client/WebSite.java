/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
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
package org.alfresco.wcm.client;

import java.io.Serializable;
import java.util.List;


/**
 * Web Site Interface
 * 
 * @author Roy Wetherall
 */
public interface WebSite extends Serializable
{
	/** Property constants */
	static final String PROP_HOSTNAME = "ws:hostName";
	static final String PROP_HOSTPORT = "ws:hostPort";
	
	/**
	 * Gets the host name
	 * 
	 * @return	String	host name
	 */
	String getHostName();
	
	/**
	 * Gets the host port
	 * 
	 * @return	int		host port
	 */
	int getHostPort();
	
	/**
	 * Gets the web sites root section
	 * 
	 * @return	Section		root section
	 */
	Section getRootSection();
	
	/**
	 * 
	 * @param path
	 * @return
	 */
	Asset getAssetByPath(String path);	
	
	/**
	 * Gets the child sections.
	 * 
	 * @return	List<Section>	child sections
	 */
	List<Section> getSections();
	
	/**
	 * Obtain the identifier of this website
	 * @return
	 */
	String getId();
	
	
	/**
	 * Get the web site's title
	 * @return
	 */
	String getTitle();

	/**
	 * Get the web site's title
	 * @return
	 */
	String getDescription();
	
	/**
	 * Get the asset which is the site logo
	 * @return
	 */
	Asset getLogo();

	/** 
	 * Get the UGC service
     * @return 
     */
	UgcService getUgcService();


}
