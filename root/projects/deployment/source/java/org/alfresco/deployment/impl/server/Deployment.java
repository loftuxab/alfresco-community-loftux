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
import java.util.Iterator;

import org.alfresco.deployment.impl.DeploymentException;

/**
 * This is a record of an ongoing deployment.
 * @author britt
 */
public class Deployment implements Iterable<DeployedFile>
{
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
     * The underlying file output stream.  Used for forcing to disk.
     */
    private FileOutputStream fFileOut;
    
    /**
     * The object output stream to which deployed files are written.
     */
    private ObjectOutputStream fOut;
    
    /**
     * The object input stream for reading in the log.
     */
    private ObjectInputStream fIn;
    
    public Deployment(Target target, 
                      String logFile)
        throws IOException
    {
        fTarget = target;
        fLogFile = logFile;
        fLastActivity = System.currentTimeMillis();
        fFileOut = new FileOutputStream(fLogFile);
        fOut = new ObjectOutputStream(fFileOut);
        fCanBeStale = true;
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
    }
    
    /**
     * Signal that the commit phase is finished and clean up.
     */
    public void finishCommit()
    {
        try
        {
            fIn.close();
            File log = new File(fLogFile);
            log.delete();
        }
        catch (IOException e)
        {
            // Do Nothing.
        }
    }
    
    public Target getTarget()
    {
        return fTarget;
    }
    
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
                fNext = (DeployedFile)fIn.readObject();
                if (fNext == null)
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
            return fNext;
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
