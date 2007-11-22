/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.module.phpIntegration.lib;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.version.VersionType;

import com.caucho.quercus.annotation.Optional;

/**
 * Version class.
 * 
 * TODO .. this needs to be filled out with the remaining methods for the other bits of meta-data
 * 
 * @author Roy Wetherall
 */
public class Version extends Node implements ScriptObject 
{
	/** The script object name */
    private static final String SCRIPT_OBJECT_NAME = "Version";    
    
    /** The version description */
    private String description;
    
    /** Indicates whether the version is a major one or not */
    private boolean major = false;
    
    /**
     * Helper method to create a version from the repository version object.
     * 
     * @param session		the session
     * @param repoVersion	the repository version object
     * @return Version		the version object
     */
    public static Version createVersion(Session session, org.alfresco.service.cmr.version.Version repoVersion)
    {
    	NodeRef versionNodeRef = repoVersion.getFrozenStateNodeRef();
    	String description = repoVersion.getDescription();
    	boolean major = VersionType.MAJOR.equals(repoVersion.getVersionType());
    	return new Version(session, versionNodeRef, description, major);
    }
    
    /**
     * Constructor
     * 
     * @param session		the session
     * @param nodeRef		the node reference
     * @param description	the description
     * @param major			indicates whether this is a major version or not
     */
    public Version(Session session, NodeRef nodeRef, @Optional("") String description, @Optional("false") boolean major)
    {
        super(session, nodeRef);
        
        this.description = description;
        this.major = major;
    }

    /**
     * Constructor
     * 
     * @param session		the session
     * @param store			the store
     * @param id			the id
     * @param description	the description
     * @param major			indicates whether this is a major version or not
     */
    public Version(Session session, Store store, String id, @Optional("") String description, @Optional("false") boolean major)
    {
    	super(session, store, id);
    	
    	this.description = description;
    	this.major = major;
    }
	
	/** 
	 * @see org.alfresco.module.phpIntegration.lib.ScriptObject#getScriptObjectName()
	 */
	public String getScriptObjectName() 
	{
		return SCRIPT_OBJECT_NAME;
	}
	
	/**
	 * Gets the description of the version
	 * 
	 * @return	the description 
	 */
	public String getDescription() 
	{
		return this.description;
	}
	
	/**
	 * Indicates whether this is a major version or not
	 * 
	 * @return	boolean	true if a major version, false otherwise
	 */
	public boolean getMajor()
	{
		return this.major;
	}
}
