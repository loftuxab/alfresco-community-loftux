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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.SortedSet;

import org.alfresco.deployment.impl.DeploymentException;
import org.alfresco.deployment.types.FileDescriptor;
import org.alfresco.deployment.util.Path;

/**
 * This represents a target for a deployment.
 * @author britt
 */
public class Target
{
    private static final String MD_NAME = ".md.";
    private static final String CLONE = "clone";

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

    public SortedSet<FileDescriptor> getListing(String path)
    {
        Path cPath = new Path(path);
        StringBuilder builder = new StringBuilder();
        builder.append(fMetaDataDirectory);
        if (cPath.size() != 0)
        {
            for (int i = 0; i < cPath.size(); i++)
            {
                builder.append(File.separatorChar);
                builder.append(cPath.get(i));
            }
        }
        builder.append(File.separatorChar);
        builder.append(MD_NAME);
        String mdPath = builder.toString();
        try
        {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(mdPath));
            DirectoryMetaData md = (DirectoryMetaData)in.readObject();
            in.close();
            return md.getListing();
        }
        catch (IOException e)
        {
            throw new DeploymentException("Could not read metadata for " + path, e);
        }
        catch (ClassNotFoundException nfe)
        {
            throw new DeploymentException("Misconfiguration: cannot instantiate DirectoryMetaData.", nfe);
        }
    }
    
    /**
     * Clone all the metadata files for the commit phase of a deployment.
     */
    public void cloneMetaData()
    {
        recursiveCloneMetaData(fMetaDataDirectory);
    }
    
    private void recursiveCloneMetaData(String dir)
    {
        try
        {
            String mdName = dir + File.separatorChar + MD_NAME;
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(mdName));
            DirectoryMetaData md = (DirectoryMetaData)in.readObject();
            in.close();
            String cloneName = mdName + CLONE;
            FileOutputStream fout = new FileOutputStream(cloneName);
            ObjectOutputStream out = new ObjectOutputStream(fout);
            out.writeObject(md);
            out.flush();
            fout.getChannel().force(true);
            out.close();
        }
        catch (IOException e)
        {
            throw new DeploymentException("Could not copy metadata for " + dir, e);
        }
        catch (ClassNotFoundException nfe)
        {
            throw new DeploymentException("Configuration error: could not instantiate DirectoryMetaData.");
        }
        File dFile = new File(dir);
        File[] listing = dFile.listFiles();
        for (File file : listing)
        {
            if (file.isDirectory())
            {
                recursiveCloneMetaData(dir + File.separatorChar + file.getName());
            }
        }
    }
}
