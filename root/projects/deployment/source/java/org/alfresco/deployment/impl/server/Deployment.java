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

import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.alfresco.deployment.impl.DeploymentException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This is a record of an ongoing deployment.
 * 
 * A collection of Deployed Files
 * 
 * @author britt
 */
public class Deployment implements Iterable<DeployedFile>, Serializable
{
	private static final long serialVersionUID = -4675002987994484069L;

	private static Log logger = LogFactory.getLog(Deployment.class);
    
    /**
     * Synchronised list of deployed files.
     */
    private List<DeployedFile> deployedFiles = new Vector<DeployedFile>();
    
    /**
     * Timestamp of last time this deployment was talked to.
     */
    private long fLastActivity;
    
    /**
     * ticket of this deployment
     */
    private String ticket;
    
    /**
     * The name of the target
     */
    private String targetName;

    /**
     * Flag for whether this deployment is in a state to be timed out.
     */
    private boolean fCanBeStale;
    
    /**
     * has a meta data error been detected in this deployment - regardless of whether it has been corrected.
     */
    private boolean metaError = false;

    /**
     * The state of this deployment with regards to the transaction.
     */
    private DeploymentState fState;
    
    /**
     * The snapshot version number from the authoring cluster.
     */
    private int authoringVersion;
    
    /**
     * The name of the source store on the authoring cluster.
     */
    private String authoringStoreName;
    
    /**
     * Create a new Deployment record
     * @param ticket the ticket for this deployment.
     * @param targetName the name of the deployment target
     * @param storeName the name of the source store on the authoring cluster.
     * @param version the snapshot version (from the authoring cluster) being deployed.
     * @throws IOException
     */
    public Deployment(String ticket, String targetName, String storeName, int version)
        throws IOException
    {
    	this.targetName = targetName;
    	this.ticket = ticket;
        fLastActivity = System.currentTimeMillis();
        fCanBeStale = true;
        fState = DeploymentState.WORKING;
        this.authoringStoreName = storeName;
        this.authoringVersion = version;
    }

    /**
     * Log that a file has been deployed.
     * @param file
     * @throws IOException
     */
    public void add(DeployedFile file)
        throws IOException
    {
    	synchronized (this) 
    	{
    		deployedFiles.add(file);
    		fLastActivity = System.currentTimeMillis();
    	}
    }

    /**
     * Prepare this deployment.
     * @throws IOException
     */
    public void prepare()
        throws IOException, DeploymentException
    {
        fCanBeStale = false;
        fState = DeploymentState.PREPARED;
    }

    /**
     * And commit is now in progress.
     */
    public void commit()
        throws IOException
    {
        fState = DeploymentState.COMMITTED;
    }

    /**
     * Mark the Deployment as aborting.
     */
    public void abort()
        throws IOException
    {

        fCanBeStale = false;
        fState = DeploymentState.ABORTED;
    }

    /**
     * Is the deployment stale.
     * @param timeout
     * @return
     */
    public boolean isStale(long timeout)
    {
        if (!fCanBeStale)
        {
            return false;
        }
        if (System.currentTimeMillis() - fLastActivity > timeout)
        {
            return true;
        }
        return false;
    }


    /**
     * get the iterator for the files contained within this deployment.
     */
    public Iterator<DeployedFile> iterator()
    {
    	return deployedFiles.iterator();
    }

    /**
     * Get the state of the deployment.
     * @return
     */
    public DeploymentState getState()
    {
        return fState;
    }
    
    /**
     * Set the state of a deployment
     * @param state
     */
    public void setDeploymentState(DeploymentState state)
    {
    	this.fState = state;
    }

    public void setMetaError(boolean metaError) 
    {
		this.metaError = metaError;
	}

    /**
     * Has there been an error detected with meta-data?
     * @return
     */
	public boolean isMetaError() 
	{
		return metaError;
	}

	/**
	 * Set the authoring version (of the deployment from the authoring server)
	 * @param authoring version
	 */
	public void setAuthoringVersion(int version) 
	{
		this.authoringVersion = version;
	}

	/**
	 * Get the version being deployed.   The version relates to the authoring cluster.
	 * @return
	 */
	public int getAuthoringVersion() 
	{
		return authoringVersion;
	}

	/**
	 * Get the store name being deployed.   The store name is from the authoring cluster.
	 * @param storeName
	 */
	public void setAuthoringStoreName(String storeName) 
	{
		this.authoringStoreName = storeName;
	}

	/**
	 * Get the name of the store being deployed.   The store name is from the authoring cluster.
	 * @return the name of the store on the authoring cluster
	 */
	public String getAuthoringStoreName() 
	{
		return authoringStoreName;
	}
}
