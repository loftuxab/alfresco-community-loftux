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

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;

import org.alfresco.deployment.FileDescriptor;
import org.alfresco.deployment.FileType;
import org.alfresco.deployment.impl.DeploymentException;
import org.alfresco.util.Deleter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This is a record of an ongoing deployment.
 * 
 * @author britt
 */
public class Deployment implements Iterable<DeployedFile>, Serializable
{
    private static final long serialVersionUID = 4752110479673700145L;
    
    private static Log logger = LogFactory.getLog(Deployment.class);

    /**
     * Timestamp of last time this deployment was talked to.
     */
    private long fLastActivity;

    /**
     * Flag for whether this deployment is in a state to be timed out.
     */
    private boolean fCanBeStale;
    
    /**
     * has a meta data error been detected in this deployment - regardless of whether it has been corrected.
     */
    private boolean metaError = false;

    /**
     * The deployment target string.
     */
    private Target fTarget;

    /**
     * The path to the log file for this deployment.
     */
    private String fLogFile;

    /**
     * The location of the log directory.
     */
    private String fLogDir;

    /**
     * The underlying file output stream.  Used for forcing to disk.
     */
    private transient FileOutputStream fFileOut;

    /**
     * The object output stream to which deployed files are written.
     */
    private transient ObjectOutputStream fOut;

    /**
     * The state of this deployment with regards to the transaction.
     */
    private DeploymentState fState;

    /**
     * Keeps track of any open output streams.
     */
    private transient Map<OutputStream, DeployedFile> fOutputStreams;

    public Deployment(Target target,
                      String logDir)
        throws IOException
    {
        fTarget = target;
        fLogDir = logDir;
        fLogFile = logDir + File.separatorChar + "log";
        fLastActivity = System.currentTimeMillis();
        File lDir = new File(logDir);
        lDir.mkdir();
        fFileOut = new FileOutputStream(fLogFile);
        fOut = new ObjectOutputStream(fFileOut);
        fCanBeStale = true;
        fState = DeploymentState.WORKING;
        fOutputStreams =  Collections.synchronizedMap(new HashMap<OutputStream, DeployedFile>());
    }

    /**
     * Tell the deployment about a file in transit.
     * @param out
     * @param file
     */
    public void addOutputStream(OutputStream out, DeployedFile file)
    {
        fOutputStreams.put(out, file);
    }

    /**
     * Get the deployed file record for the output stream.
     * @param out
     * @return the file being transmitted
     */
    public DeployedFile getDeployedFile(OutputStream out)
    {
    	return fOutputStreams.get(out);
    }

    /**
     * close the output stream
     * @param out
     * @throws IOException
     */
    public void closeOutputStream(OutputStream out)
        throws IOException
    {
        out.flush();
        out.close();
        if (fOutputStreams.remove(out) == null)
        {
            throw new DeploymentException("Closed unknown file.");
        }
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
    		fOut.writeObject(file);
    		fLastActivity = System.currentTimeMillis();
    	}
    }

    /**
     * Prepare this deployment.
     * @throws IOException
     */
    public void prepare()
        throws IOException
    {
        fOut.flush();
        fFileOut.getChannel().force(true);
        fOut.close();
        fCanBeStale = false;
        fTarget.cloneMetaData(this);
        fState = DeploymentState.PREPARING;
    }

    /**
     * Signal that the prepare phase of this deployment is finished.
     * And commit is now in progress.
     */
    public void finishPrepare()
        throws IOException
    {
        fState = DeploymentState.COMMITTING;
    }

    /**
     * Mark the Deployment as aborting.
     */
    public void abort()
        throws IOException
    {
        fOut.flush();
        fFileOut.getChannel().force(true);
        fOut.close();
        fCanBeStale = false;
        fState = DeploymentState.ABORTING;
        for (OutputStream out : fOutputStreams.keySet())
        {
        	out.flush();
            out.close();
        }
        fOutputStreams.clear();
        
        //TODO Need to gather reader threads here
        for (DeployedFile file : this)
        {
            if (file.getType() == FileType.FILE)
            {
                File toDelete = new File(file.getPreLocation());
                toDelete.delete();
            }
        }
        File logDir = new File(fLogDir);
        Deleter.Delete(logDir);
    }

    /**
     * Get the Target of this deployment.
     * @return
     */
    public Target getTarget()
    {
        return fTarget;
    }

    /**
     * Signal that the commit phase is finished and clean up.
     */
    public void finishCommit()
    {
            fTarget.commitMetaData(this);

            fTarget.runPostCommit(this);
            File logDir = new File(fLogDir);
            Deleter.Delete(logDir);
    }

    /**
     * Rollback this deployment.
     */
    public void rollback()
    {

            fTarget.rollbackMetaData();
            Deleter.Delete(fLogDir);
    }

    /**
     * Get the target relative File.
     * @param path
     * @return
     */
    public File getFileForPath(String path)
    {
        return fTarget.getFileForPath(path);
    }

    /**
     * Get a listing for a directory. This is the predeployment listing.
     * @param path
     * @return
     */
    public SortedSet<FileDescriptor> getListing(String path)
    {
        return fTarget.getListing(path);
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

    /* (non-Javadoc)
     * @see java.lang.Iterable#iterator()
     */
    public Iterator<DeployedFile> iterator()
    {
        return new DeployedFileIterator();
    }

    public void setGuid(String path, String guid)
        throws IOException
    {
        DeployedFile file = new DeployedFile(FileType.SETGUID,
                                             null,
                                             path,
                                             guid,
                                             false);
        fOut.writeObject(file);
    }

    /**
     * Get the state of the deployment.
     * @return
     */
    public DeploymentState getState()
    {
        return fState;
    }

    public void setMetaError(boolean metaError) {
		this.metaError = metaError;
	}

	public boolean isMetaError() {
		return metaError;
	}

	/**
     * Iterator for reading back the log.
     * @author britt
     */
    private class DeployedFileIterator implements Iterator<DeployedFile>
    {
        private DeployedFile fNext = null;
        
        private ObjectInputStream fIn;

        public DeployedFileIterator()
        {
        	try {
				fIn = new ObjectInputStream(new FileInputStream(fLogFile));
			} catch (FileNotFoundException e) {
				throw new DeploymentException("FileNotFound. logFile:" + fLogFile, e);
			} catch (IOException e) {
			    throw new DeploymentException("I/O error.", e);
			}
            
            fNext = getNext();
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#hasNext()
         */
        public boolean hasNext()
        {
        	return fNext != null;
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#next()
         */
        public DeployedFile next()
        {
            DeployedFile next = fNext;
            
            fNext = getNext();
            
            return next;    
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#remove()
         */
        public void remove()
        {
            throw new RuntimeException("Not Implemented.");
        }
        
        private DeployedFile getNext() 
        {
           try
           {
        	   DeployedFile next = (DeployedFile)fIn.readObject();
               return next;
           }
           catch (EOFException eofe)
           {
                return null;

           }
           catch (IOException e)
           {
                throw new DeploymentException("I/O error.", e);
           }
           catch (ClassNotFoundException nfe)
           {
                throw new DeploymentException("Unable to read deployed file:" + nfe.toString(), nfe);
           }
        }
        
        public void finalize() {
        	try {
        		if(fIn != null)
        		{
        			fIn.close();
        			fIn = null;
        		}
        	}
        	catch (Throwable t)
        	{
        		logger.error("Unable to finalize", t);
        	}
        }
    }
}
