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
import org.alfresco.deployment.FileDescriptor;
import org.alfresco.deployment.FileType;
import org.alfresco.deployment.config.Configuration;
import org.alfresco.deployment.impl.DeploymentException;
import org.alfresco.util.Deleter;
import org.alfresco.util.GUID;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Server implementation.
 * @author britt
 */
public class DeploymentReceiverServiceImpl implements DeploymentReceiverService, Runnable, 
    ApplicationContextAware
{
    private ConfigurableApplicationContext fContext;
    
    private Configuration fConfiguration;
    
    private Thread fThread;
    
    private boolean fDone;
    
    private Map<String, Deployment> fDeployments;
    
    private Map<String, Boolean> fTargetBusy;
    
    public DeploymentReceiverServiceImpl()
    {
        fDone = false;
        fDeployments = new HashMap<String, Deployment>();
        fTargetBusy = new HashMap<String, Boolean>();
    }

    public void setConfiguration(Configuration config)
    {
        fConfiguration = config;
    }
    
    public void init()
    {
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
        try
        {
            fThread.join();
        }
        catch (InterruptedException e)
        {
            // Do nothing.
        }
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.deployment.DeploymentReceiverService#abort(java.lang.String)
     */
    public synchronized void abort(String ticket)
    {
        Deployment deployment = fDeployments.get(ticket);
        if (deployment == null)
        {
            throw new DeploymentException("Deployment timed out or invalid token.");  
        }
        if (deployment.getState() != DeploymentState.WORKING)
        {
            throw new DeploymentException("Deployment cannot be aborted: already aborting, or committing.");
        }
        try
        {
            deployment.abort();
            Target target = deployment.getTarget();
            fTargetBusy.put(target.getName(), false);
            fDeployments.remove(ticket);
        }
        catch (IOException e)
        {
            throw new DeploymentException("Traumatic failure. Could not abort cleanly.");
        }
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
                // TODO How to we recover from this?
                throw new DeploymentException("Could not create logfile; Deployment cannot continue", e);
            }
            fTargetBusy.put(targetName, true);
            return ticket;
        }
    }

    /* (non-Javadoc)
     * @see org.alfresco.deployment.DeploymentReceiverService#commit(java.lang.String)
     */
    public synchronized void commit(String ticket)
    {
        Deployment deployment = fDeployments.get(ticket);
        if (deployment == null)
        {
            throw new DeploymentException("Could not commit because deployment timed out.");
        }
        try
        {
            deployment.finishWork();
            // First phase.  Perform all mkdirs and file sends by renaming any
            // existing file/directory to *.alf, and by simply renaming any deleted
            // files/directories to *.alf.
            for (DeployedFile file : deployment)
            {
                deployment.update(file);
                String path = file.getPath();
                switch (file.getType())
                {
                    case DIR :
                    {
                        File f = deployment.getFileForPath(path);
                        if (f.exists())
                        {
                            File dest = new File(f.getAbsolutePath() + ".alf");
                            f.renameTo(dest);
                            f = deployment.getFileForPath(path);;
                        }
                        f.mkdir();
                        break;
                    }
                    case FILE :
                    {
                        File f = deployment.getFileForPath(path);
                        if (f.exists())
                        {
                            File dest = new File(f.getAbsolutePath() + ".alf");
                            f.renameTo(dest);
                            f = deployment.getFileForPath(path);
                        }
                        FileOutputStream out = new FileOutputStream(f);
                        InputStream in = new FileInputStream(file.getPreLocation());
                        byte[] buff = new byte[8192];
                        int read = 0;
                        while ((read = in.read(buff)) != -1)
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
                        File f = deployment.getFileForPath(path);
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
                if (file.getType() == FileType.FILE)
                {
                    File intermediate = new File(file.getPreLocation());
                    intermediate.delete();
                }
                File old = new File(deployment.getFileForPath(file.getPath()).getAbsolutePath() + ".alf");
                Deleter.Delete(old);
            }
            File preLocation = new File(fConfiguration.getDataDirectory() + File.separatorChar + ticket);
            preLocation.delete();
            deployment.finishCommit();
            fDeployments.remove(ticket);
            fTargetBusy.put(deployment.getTarget().getName(), false);
        }
        catch (Exception e)
        {
            fDeployments.remove(ticket);
            fTargetBusy.put(deployment.getTarget().getName(), false);
            throw new DeploymentException("Problem finishing transaction work; try recovery.", e);
        }
    }

    /* (non-Javadoc)
     * @see org.alfresco.deployment.DeploymentReceiverService#delete(java.lang.String, java.lang.String)
     */
    public synchronized void delete(String token, String path)
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
            abort(token);
            throw new DeploymentException("Could not update log. Aborted.", e);
        }
    }

    /* (non-Javadoc)
     * @see org.alfresco.deployment.DeploymentReceiverService#finishSend(java.lang.String, java.io.OutputStream)
     */
    public synchronized void finishSend(String token, OutputStream out)
    {
        Deployment deployment = fDeployments.get(token);
        if (deployment == null)
        {
            throw new DeploymentException("Deployment timed out or invalid ticket.");
        }
        try
        {
            DeployedFile file = deployment.getOutputFile(out);
            deployment.closeOutputFile(out);
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
    public synchronized List<FileDescriptor> getListing(String ticket, String path)
    {
        Deployment deployment = fDeployments.get(ticket);
        if (deployment == null)
        {
            throw new DeploymentException("Deployment timed out or invalid ticket.");
        }
        try
        {
            return new ArrayList<FileDescriptor>(deployment.getListing(path));
        }
        catch (Exception e)
        {
            abort(ticket);
            throw new DeploymentException("Could not get listing for " + path + ". Aborted.", e);
        }
    }

    /* (non-Javadoc)
     * @see org.alfresco.deployment.DeploymentReceiverService#mkdir(java.lang.String, java.lang.String, java.lang.String)
     */
    public synchronized void mkdir(String token, String path, String guid)
    {
        Deployment deployment = fDeployments.get(token);
        if (deployment == null)
        {
            throw new DeploymentException("Deployment timed out or invalid token.");
        }
        DeployedFile file = new DeployedFile(FileType.DIR,
                                             null,
                                             path,
                                             guid);
        try
        {
            deployment.add(file);
        }
        catch (IOException e)
        {
            abort(token);
            throw new DeploymentException("Could not log mkdir of " + path + ". Aborted.");
        }
    }

    /* (non-Javadoc)
     * @see org.alfresco.deployment.DeploymentReceiverService#send(java.lang.String, java.lang.String, java.lang.String)
     */
    public synchronized OutputStream send(String ticket, String path, String guid)
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
                                                 path,
                                                 guid);
            deployment.addOutputFile(out, file);
            return out;
        }
        catch (IOException e)
        {
            abort(ticket);
            throw new DeploymentException("Could Not Open " + path + " for write. Aborted.", e);
        }
    }

    /* (non-Javadoc)
     * @see org.alfresco.deployment.DeploymentReceiverService#shutDown(java.lang.String, java.lang.String)
     */
    public synchronized void shutDown(String user, String password)
    {
        fContext.close();
        System.exit(0);
    }

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    public void run()
    {
        while (!fDone)
        {
            try
            {
                Thread.sleep(2000);
            }
            catch (InterruptedException e)
            {
                // Do Nothing.
            }
        }
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        fContext = (ConfigurableApplicationContext)applicationContext;
    }
}
