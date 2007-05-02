/*
 * Copyright (C) 2005-2007 Alfresco Software Limited.
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
 * http://www.alfresco.com/legal/licensing
 */

package org.alfresco.deployment.impl.server;

import java.io.File;

/**
 * This represents a target for a deployment.
 * @author britt
 */
public class Target
{
    /**
     * The name of the target.
     */
    private String fTargetName;
    
    /**
     * The root directory of the target deployment.
     */
    private String fRootDirectory;
    
    /**
     * Where metadata is kept for this target.
     */
    private String fMetaDataDirectory;
    
    /**
     * The user name for authenticating to this target.
     */
    private String fUser;
    
    /**
     * The password for authenticating to this target.
     */
    private String fPassword;
    
    /**
     * Make one up.
     * @param name
     * @param root
     * @param metadata
     */
    public Target(String name,
                  String root,
                  String metadata,
                  String user,
                  String password)
    {
        fTargetName = name;
        fRootDirectory = root;
        fMetaDataDirectory = metadata;
        fUser = user;
        fPassword = password;
    }
    
    /**
     * Get the target name.
     * @return
     */
    public String getName()
    {
        return fTargetName;
    }
    
    /**
     * Get the root directory.
     * @return
     */
    public String getRootDirectory()
    {
        return fRootDirectory;
    }
    
    /**
     * Get the meta data directory.
     * @return
     */
    public String getMetaDataDirectory()
    {
        return fMetaDataDirectory;
    }
    
    /**
     * Get the username for this target.
     * @return
     */
    public String getUser()
    {
        return fUser;
    }
    
    /**
     * Get the password for this target.
     * @return
     */
    public String getPassword()
    {
        return fPassword;
    }
    
    /**
     * Get a File object for the given path in this target.
     * @param path
     * @return
     */
    public File getFileForPath(String path)
    {
        return new File(fRootDirectory + normalizePath(path));
    }
    
    /**
     * Utility to normalize a path to platform specific form.
     * @param path
     * @return
     */
    private String normalizePath(String path)
    {
        path = path.replaceAll("/+", File.separator);
        path = path.replace("/$", "");
        if (!path.startsWith(File.separator))
        {
            path = File.separator + path;
        }
        return path;
    }
}
