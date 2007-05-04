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
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.deployment.DeploymentReceiverService;
import org.alfresco.deployment.config.Configuration;
import org.alfresco.deployment.impl.DeploymentException;
import org.alfresco.deployment.types.FileDescriptor;
import org.alfresco.deployment.types.FileType;
import org.alfresco.util.GUID;

/**
 * Server implementation.
 * @author britt
 */
public class DeploymentReceiverServiceImpl implements DeploymentReceiverService, Runnable
{
    private Configuration fConfiguration;
    
    private Thread fThread;
    
    private boolean fDone;
    
    private Map<String, Deployment> fDeployments;
    
    private Map<String, Boolean> fTargetBusy;
    
    private Map<OutputStream, DeployedFile> fOutputStreamFiles;
    
    public DeploymentReceiverServiceImpl()
    {
        fDone = false;
        fDeployments = new HashMap<String, Deployment>();
        fTargetBusy = new HashMap<String, Boolean>();
        fOutputStreamFiles = new HashMap<OutputStream, DeployedFile>();
    }

    public void setConfiguration(Configuration config)
    {
        fConfiguration = config;
    }
    
    public void init()
    {
        // TODO Incomplete.
        for (String target : fConfiguration.getTargetNames())
        {
            fTargetBusy.put(target, false);
        }
        fThread = new Thread(this);
        fThread.start();   
    }
    
    public void shutDown()
    {
        fDone = true;
        synchronized (this)
        {
            this.notifyAll();
        }
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.deployment.DeploymentReceiverService#abort(java.lang.String)
     */
    public void abort(String token)
    {
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see org.alfresco.deployment.DeploymentReceiverService#begin(java.lang.String, java.lang.String, java.lang.String)
     */
    public String begin(String targetName, String user, String password)
    {
        Target target = fConfiguration.getTarget(targetName);
        if (target == null)
        {
            throw new DeploymentException("No such target: " + targetName);
        }
        if (!user.equals(target.getUser()) || !password.equals(target.getPassword()))
        {
            throw new DeploymentException("Invalid user name or password.");
        }
        synchronized (this)
        {
            if (fTargetBusy.get(targetName))
            {
                throw new DeploymentException("Deployment in progress to " + targetName);
            }
            String ticket = GUID.generate();
            try
            {
                Deployment deployment = 
                    new Deployment(target, fConfiguration.getLogDirectory() + File.separator + ticket);
                fDeployments.put(ticket, deployment);
            }
            catch (IOException e)
            {
                throw new DeploymentException("Could not create logfile; Deployment cannot continue", e);
            }
            fTargetBusy.put(targetName, true);
            return ticket;
        }
    }

    /* (non-Javadoc)
     * @see org.alfresco.deployment.DeploymentReceiverService#commit(java.lang.String)
     */
    public void commit(String ticket)
    {
        Deployment deployment = fDeployments.get(ticket);
        if (deployment == null)
        {
            throw new DeploymentException("Could not commit because deployment timed out.");
        }
        try
        {
            // TODO Work out updating of metadata files.
            deployment.finishWork();
            deployment.getTarget().cloneMetaData();
            // First phase.  Perform all mkdirs and file sends by renaming any
            // existing file/directory to *.alf, and by simply renaming any deleted
            // files/directoris to *.alf.
            for (DeployedFile file : deployment)
            {
                switch (file.getType())
                {
                    case DIR :
                    {
                        File f = new File(file.getFinalPath());
                        if (f.exists())
                        {
                            File dest = new File(f.getAbsolutePath() + ".alf");
                            f.renameTo(dest);
                            f = new File(file.getFinalPath());
                        }
                        f.mkdir();
                        break;
                    }
                    case FILE :
                    {
                        File f = new File(file.getFinalPath());
                        if (f.exists())
                        {
                            File dest = new File(f.getAbsolutePath() + ".alf");
                            f.renameTo(dest);
                            f = new File(file.getFinalPath());
                        }
                        FileOutputStream out = new FileOutputStream(f);
                        InputStream in = new FileInputStream(file.getPreLocation());
                        byte[] buff = new byte[8192];
                        int read = 0;
                        while ((read = in.read(buff)) != 0)
                        {
                            out.write(buff, 0, read);
                        }
                        in.close();
                        out.flush();
                        out.getChannel().force(true);
                        out.close();
                        break;
                    }
                    case DELETED :
                    {
                        File f = new File(file.getFinalPath());
                        if (f.exists())
                        {
                            File dest = new File(f.getAbsolutePath() + ".alf");
                            f.renameTo(dest);
                        }
                        break;
                    }
                    default :
                    {
                        throw new DeploymentException("Internal error: unknown file type: " + file.getType());
                    }
                }
            }
            // Phase 2 : Go through the log again and remove all .alf entries
            deployment.resetLog();
            for (DeployedFile file : deployment)
            {
                File intermediate = new File(file.getPreLocation());
                intermediate.delete();
                File old = new File(file.getFinalPath() + ".alf");
                purge(old);
            }
            deployment.finishCommit();
            fDeployments.remove(ticket);
        }
        catch (Exception e)
        {
            // TODO Clean out all accumulated junk.
            fDeployments.remove(ticket);
            throw new DeploymentException("Problem finishing transaction work; deployment aborted.");
        }
        // TODO Now do the commit work.
    }

    /**
     * Purge the old version of a file if it exists.
     * @param file
     */
    private void purge(File file)
    {
        // TODO Implement.   
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.deployment.DeploymentReceiverService#delete(java.lang.String, java.lang.String)
     */
    public void delete(String token, String path)
    {
        Deployment deployment = fDeployments.get(token);
        if (deployment == null)
        {
            throw new DeploymentException("Deployment Timed Out.");
        }
        try
        {
            DeployedFile file = new DeployedFile(FileType.DELETED, 
                                                 null,
                                                 path,
                                                 null);
            deployment.add(file);
        }
        catch (IOException e)
        {
            // TODO cleanup deployment.
            throw new DeploymentException("Could not update log.", e);
        }
    }

    /* (non-Javadoc)
     * @see org.alfresco.deployment.DeploymentReceiverService#finishSend(java.lang.String, java.io.OutputStream)
     */
    public void finishSend(String token, OutputStream out, String guid)
    {
        Deployment deployment = fDeployments.get(token);
        if (deployment == null)
        {
            throw new DeploymentException("Deployment timed out or invalid ticket.");
        }
        try
        {
            out.flush();
            ((FileOutputStream)out).getChannel().force(true);
            out.close();
            DeployedFile file = fOutputStreamFiles.get(out);
            if (file == null)
            {
                // TODO Do cleanup.
                throw new DeploymentException("Closing Unknown OutputStream.");
            }
            deployment.add(file);
        }
        catch (IOException e)
        {
            // TODO Do cleanup.
            throw new DeploymentException("I/O error closing sent file and logging.");
        }
    }

    /* (non-Javadoc)
     * @see org.alfresco.deployment.DeploymentReceiverService#getListing(java.lang.String, java.lang.String)
     */
    public List<FileDescriptor> getListing(String ticket, String path)
    {
        Deployment deployment = fDeployments.get(ticket);
        if (deployment == null)
        {
            throw new DeploymentException("Deployment timed out or invalid ticket.");
        }
        return new ArrayList<FileDescriptor>(deployment.getTarget().getListing(path));
    }

    /* (non-Javadoc)
     * @see org.alfresco.deployment.DeploymentReceiverService#mkdir(java.lang.String, java.lang.String, java.lang.String)
     */
    public void mkdir(String token, String path, String guid)
    {
        Deployment deployment = fDeployments.get(token);
        if (deployment == null)
        {
            throw new DeploymentException("Deployment timed out or invalid token.");
        }
        DeployedFile file = new DeployedFile(FileType.DIR,
                                             null,
                                             deployment.getTarget().getFileForPath(path).getAbsolutePath(),
                                             guid);
        try
        {
            deployment.add(file);
        }
        catch (IOException e)
        {
            throw new DeploymentException("Could not log mkdir of " + path);
        }
    }

    /* (non-Javadoc)
     * @see org.alfresco.deployment.DeploymentReceiverService#send(java.lang.String, java.lang.String, java.lang.String)
     */
    public OutputStream send(String ticket, String path, String guid)
    {
        Deployment deployment = fDeployments.get(ticket);
        if (deployment == null)
        {
            throw new DeploymentException("Deployment timed out or invalid ticket.");
        }
        try
        {
            String preLocation = fConfiguration.getDataDirectory() + File.separator + guid;
            OutputStream out = new FileOutputStream(preLocation);
            DeployedFile file = new DeployedFile(FileType.FILE,
                                                 preLocation,
                                                 deployment.getTarget().getFileForPath(path).getAbsolutePath(),
                                                 guid);
            fOutputStreamFiles.put(out, file);
            return out;
        }
        catch (IOException e)
        {
            throw new DeploymentException("Could Not Open " + path + " for write.", e);
        }
    }

    /* (non-Javadoc)
     * @see org.alfresco.deployment.DeploymentReceiverService#shutDown(java.lang.String, java.lang.String)
     */
    public void shutDown(String user, String password)
    {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    public void run()
    {
        while (!fDone)
        {
            
        }
    }
}
