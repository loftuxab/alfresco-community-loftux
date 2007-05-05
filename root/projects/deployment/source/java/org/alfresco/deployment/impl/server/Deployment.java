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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;

import org.alfresco.deployment.FileDescriptor;
import org.alfresco.deployment.FileType;
import org.alfresco.deployment.impl.DeploymentException;
import org.alfresco.deployment.util.Deleter;

/**
 * This is a record of an ongoing deployment.
 * @author britt
 */
public class Deployment implements Iterable<DeployedFile>, Serializable
{
    private static final long serialVersionUID = 4752110479673700145L;

    /**
     * Timestamp of last time this deployment was talked to.
     */
    private long fLastActivity;
    
    /**
     * Flag for whether this deployment is in a state to be timed out.
     */
    private boolean fCanBeStale;
    
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
     * The path to the persistent storage of this Deployment.
     */
    private String fDepFile;
    
    /**
     * The underlying file output stream.  Used for forcing to disk.
     */
    private transient FileOutputStream fFileOut;
    
    /**
     * The object output stream to which deployed files are written.
     */
    private transient ObjectOutputStream fOut;
    
    /**
     * The object input stream for reading in the log.
     */
    private transient ObjectInputStream fIn;
    
    /**
     * The state of this deployment with regards to the transaction.
     */
    private DeploymentState fState;
    
    /**
     * Keeps track of any open output files.
     */
    private transient Map<OutputStream, DeployedFile> fOutputFiles;
    
    public Deployment(Target target, 
                      String logDir)
        throws IOException
    {
        fTarget = target;
        fLogDir = logDir;
        fLogFile = logDir + File.separatorChar + "log";
        fDepFile = logDir + File.separatorChar + "deployment";
        fLastActivity = System.currentTimeMillis();
        File lDir = new File(logDir);
        lDir.mkdir();
        fFileOut = new FileOutputStream(fLogFile);
        fOut = new ObjectOutputStream(fFileOut);
        fCanBeStale = true;
        fState = DeploymentState.WORKING;
        fOutputFiles = new HashMap<OutputStream, DeployedFile>();
        save();
    }
    
    /**
     * Tell the deployment about a file in transit.
     * @param out
     * @param file
     */
    public void addOutputFile(OutputStream out, DeployedFile file)
    {
        fOutputFiles.put(out, file);
    }
    
    /**
     * Get the deployed file record for the output stream.
     * @param out
     * @return
     */
    public DeployedFile getOutputFile(OutputStream out)
    {
        return fOutputFiles.get(out);
    }
    
    public void closeOutputFile(OutputStream out)
        throws IOException
    {
        out.flush();
        ((FileOutputStream)out).getChannel().force(true);
        out.close();
        if (fOutputFiles.remove(out) == null)
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
        fOut.writeObject(file);
        fLastActivity = System.currentTimeMillis();
    }
    
    /**
     * Signal that the pre-commit phase of this deployment is finished.
     * @throws IOException
     */
    public void finishWork()
        throws IOException
    {
        fOut.flush();
        fFileOut.getChannel().force(true);
        fOut.close();
        fIn = new ObjectInputStream(new FileInputStream(fLogFile));
        fCanBeStale = false;
        fTarget.cloneMetaData();
        fState = DeploymentState.COMMITTING;
        save();
    }
    
    public void resetLog()
        throws IOException
    {
        fIn.close();
        fIn = new ObjectInputStream(new FileInputStream(fLogFile));
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
        fIn = new ObjectInputStream(new FileInputStream(fLogFile));
        fCanBeStale = false;
        fState = DeploymentState.ABORTING;
        for (OutputStream out : fOutputFiles.keySet())
        {
            out.close();
        }
        for (DeployedFile file : this)
        {
            if (file.getType() == FileType.FILE)
            {
                File toDelete = new File(file.getPreLocation());
                toDelete.delete();
            }
        }
        fIn.close();
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
        try
        {
            fTarget.commitMetaData();
            fIn.close();
            File logDir = new File(fLogDir);
            Deleter.Delete(logDir);
        }
        catch (IOException e)
        {
            // Do Nothing.
        }
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
     * Report a deployed file during the commit phase.
     * @param file
     */
    public void update(DeployedFile file)
    {
        fTarget.update(file);
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
    
    /**
     * Get the state of the deployment.
     * @return
     */
    public DeploymentState getState()
    {
        return fState;
    }

    /**
     * Save this record to its persistent location.
     * @throws IOException
     */
    private void save()
        throws IOException
    {
        // TODO rename, save, delete?
        FileOutputStream fout = new FileOutputStream(fDepFile);
        ObjectOutputStream out = new ObjectOutputStream(fout);
        out.writeObject(this);
        out.flush();
        fout.getChannel().force(true);
    }
    
    /**
     * Iterator for reading back the log.
     * @author britt
     */
    private class DeployedFileIterator implements Iterator<DeployedFile>
    {
        private DeployedFile fNext;

        public DeployedFileIterator()
        {
            fNext = null;
        }
        
        /* (non-Javadoc)
         * @see java.util.Iterator#hasNext()
         */
        public boolean hasNext()
        {
            if (fNext != null)
            {
                return true;
            }
            try
            {
                try
                {
                    fNext = (DeployedFile)fIn.readObject();
                }
                catch (EOFException eofe)
                {
                    return false;
                }
                return true;
            }
            catch (IOException e)
            {
                throw new DeploymentException("I/O error.", e);
            }
            catch (ClassNotFoundException nfe)
            {
                throw new DeploymentException("Catastrophic Failure.", nfe);
            }
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#next()
         */
        public DeployedFile next()
        {
            DeployedFile next = fNext;
            fNext = null;
            return next;
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#remove()
         */
        public void remove()
        {
            throw new RuntimeException("Not Implemented.");
        }
    }
}
