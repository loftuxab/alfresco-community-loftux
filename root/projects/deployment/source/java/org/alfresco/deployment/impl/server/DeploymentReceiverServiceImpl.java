/*
 * Copyright (C) 2005-2008 Alfresco Software Limited.
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
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.alfresco.deployment.DeploymentReceiverService;
import org.alfresco.deployment.DeploymentTransportInputFilter;
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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This is the implementation of the Alfresco File System Receiver
 * 
 * @author britt
 */
public class DeploymentReceiverServiceImpl implements DeploymentReceiverService, Runnable, 
    ApplicationContextAware
{
    private static final boolean INJECT_PREPARE_FAILURE = false;
    
    private static final boolean INJECT_COMMIT_FAILURE = false;
    
    private ConfigurableApplicationContext fContext;
    
    private Configuration fConfiguration;
    
    private Thread fThread;
    
    private boolean fDone;
    
    private ConcurrentLinkedQueue<Target> validateQueue = new ConcurrentLinkedQueue<Target>();
    
    private Map<String, Deployment> fDeployments;
        
    private static ReaderManagement fReaders = new ReaderManagement();
    
    private static Log logger = LogFactory.getLog(DeploymentReceiverServiceImpl.class);
    
    private boolean errorOnOverwrite = false;
    
    public DeploymentReceiverServiceImpl()
    {
        fDone = false;
        fDeployments = new HashMap<String, Deployment>();
    }

    public void setConfiguration(Configuration config)
    {
        fConfiguration = config;
    }
    
    public void init()
    {
    	logger.info("Initialising Implementation");
    	logger.debug("configuration dataDirectory:" + fConfiguration.getDataDirectory());
    	logger.debug("configuration metaDataDirectory:" + fConfiguration.getMetaDataDirectory());
    	logger.debug("configuration logDirectory:" + fConfiguration.getLogDirectory());
    	
        for (String target : fConfiguration.getTargetNames())
        {
        	logger.debug("configuration target:" + target);
            validateQueue.add(fConfiguration.getTarget(target));
        }
        fThread = new Thread(this);
        fThread.setName("FSR Keep Alive");
        fThread.start();   
    }
    
    public void shutDown()
    {
    	logger.info("Shutting down Implementation");
    	
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
        	logger.error("Unable to join implementation thread while shutting down", e);
        }
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.deployment.DeploymentReceiverService#abort(java.lang.String)
     */
    public synchronized void abort(String ticket)
    {
      	logger.info("Abort ticket: " + ticket);
        Deployment deployment = fDeployments.get(ticket);
        if (deployment == null)
        {
        	logger.debug("Could not abort: invalid token ticket:" + ticket);
        	// We are most likely to get here because we are aborting an already aborted ticket
        	return;
        }
        if (deployment.getState() != DeploymentState.WORKING)
        {
            throw new DeploymentException("Deployment cannot be aborted: already aborting, or committing.");
        }
        try
        {
            deployment.abort();
        }
        catch (IOException e)
        {
        	logger.error("Error while aborting ticket:" + ticket, e);
            throw new DeploymentException("Could not abort.", e);
        }
        finally 
        {
            Target target = deployment.getTarget();
            synchronized(target) {
            	target.setBusy(false);
            }
            
            if(deployment.isMetaError())
            {
            	validateQueue.add(target);
            }
            
            fDeployments.remove(ticket);
        }
    }

    /* (non-Javadoc)
     * @see org.alfresco.deployment.DeploymentReceiverService#begin(java.lang.String, java.lang.String, java.lang.String)
     */
    public String begin(String targetName, String user, String password)
    {
    	logger.debug("begin of target:" + targetName);
        Target target = fConfiguration.getTarget(targetName);
        if (target == null)
        {
        	logger.warn("No such target:" + targetName);
            throw new DeploymentException("No such target: " + targetName);
        }
        if (!user.equals(target.getUser()) || !password.equals(target.getPassword()))
        {
        	logger.warn("Invalid user name or password");
            throw new DeploymentException("Invalid user name or password.");
        }
        
        // Check that the root directory exists
        File root = new File(target.getRootDirectory()); 
        if(!root.exists())
        {
        	 throw new DeploymentException("Root directory does not exist. rootDirectory:" + target.getRootDirectory()); 
        }
        
        synchronized (target) 
        {
            if (target.isBusy())
            {
                throw new DeploymentException("Deployment in progress to " + targetName);
            }
            String ticket = GUID.generate();
            logger.debug("begin deploy, target:" + targetName + ", ticket:" + ticket);
            
            try
            {
                Deployment deployment = 
                    new Deployment(target, fConfiguration.getLogDirectory() + File.separator + ticket);
                fDeployments.put(ticket, deployment);
            }
            catch (IOException e)
            {
            	logger.error("Could not create logfile", e);
                throw new DeploymentException("Could not create logfile; Deployment cannot continue", e);
            }
            target.setBusy(true);
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
        	String msg = "Could not commit because invalid ticket:" + ticket;
        	logger.error(msg);
            throw new DeploymentException(msg);
        }
        logger.debug("commit ticket:" + ticket);
        try
        {
        	LinkedBlockingQueue<DeployedFile> commitQueue = new LinkedBlockingQueue<DeployedFile>();
            
        	// Parallel processing here to reduce the overall time taken for commit. 
        	CommitThread commitThreads[] = 
        	{
                    new CommitMetaClonerThread(deployment),
                    new CommitWriterThread(deployment, commitQueue),
                    new CommitWriterThread(deployment, commitQueue),
                    new CommitWriterThread(deployment, commitQueue)
        	};
        			
            logger.debug("starting deployment.");

            try 
            {
            	for(int i = 0; i < commitThreads.length; i++)
            	{
            		commitThreads[i].start();	
            	}
            
                // Rename any existing soon to be overwritten files and directories with *.alf.
                // Copy in new files and directories into their final locations.
                for (DeployedFile file : deployment)
                {

                    String path = file.getPath();
                    switch (file.getType())
                    {
                        case DIR :
                        {
                            File f = deployment.getFileForPath(path);
                            if(f.exists())
                            {
                            	if(f.isFile())
                            	{
                            		File dest = new File(f.getAbsolutePath() + ".alf");
                            		f.renameTo(dest);
                            		f = deployment.getFileForPath(path);
                            		f.mkdir();
                            	}
                            }
                            else 
                            {
                            	// create a new dir
                            	f.mkdir();
                            }
                           
                            break;
                        }
                        case FILE :
                        {
                    	    commitQueue.add(file);
                            break;
                        }
                        case DELETED :
                        {
                    	    commitQueue.add(file);
                            break;
                        }
                        case SETGUID :
                        {
                            // Do nothing.
                            break;
                        }
                        default :
                        {
                    	    logger.error("Internal error: unknown file type: " + file.getType());
                            throw new DeploymentException("Internal error: unknown file type: " + file.getType());
                        }
                    }
                }
            } 
            finally 
            {
            	for(int i = 0; i < commitThreads.length; i++)
            	{
            		commitThreads[i].setFinish();	
            	}
            }
            
            for(int i = 0; i < commitThreads.length; i++)
            {
                commitThreads[i].join();
                if(commitThreads[i].getException() != null){
                	throw commitThreads[i].getException();
                }
            }
            
            if (INJECT_PREPARE_FAILURE)
            {
                throw new DeploymentException("Injected Prepare Failure");
            }
            
            // here we could still roll back
            
            deployment.finishPrepare();
     
            // Now we are past the point of no return and must go forward.
            logger.debug("committed - clean up");
            
            // Phase 2 : Go through the log again and remove all .alf entries
            for (DeployedFile file : deployment)
            {
                if (file.getType() == FileType.FILE)
                {
                    File intermediate = new File(file.getPreLocation());
                    intermediate.delete();
                    if (INJECT_COMMIT_FAILURE)
                    {
                        throw new DeploymentException("Injected Commit Failure.");
                    }
                }
                File old = new File(deployment.getFileForPath(file.getPath()).getAbsolutePath() + ".alf");
                Deleter.Delete(old);
            }
            File preLocation = new File(fConfiguration.getDataDirectory() + File.separatorChar + ticket);
            preLocation.delete();
            
            // commit meta-data and run post commit actions
            deployment.finishCommit();

            logger.debug("commited successfully ticket:" + ticket);
        }
        catch (Exception e)
        {
            if (!recover(ticket, deployment))
            {
            	logger.error("Failure during commit phase; rolled back.", e);
                throw new DeploymentException("Failure during commit phase; rolled back.", e);
            }
        }
        finally
        {
        	Target target = deployment.getTarget();
            synchronized(target) {
            	target.setBusy(false);
            }
            
            if(deployment.isMetaError())
            {
            	validateQueue.add(target);
            }
            fDeployments.remove(ticket);
        }
    }

    /**
     * Attempt to recover from an error condition sometime during 
     * prepare or commit.
     * @param ticket The deployment ticket.
     * @param deployment The deployment object.
     * @return Whether the deployment was committed or rolled back.
     */
    private synchronized boolean recover(String ticket, Deployment deployment)
    {
        try
        {
            switch (deployment.getState())
            {
                // In these two cases, we recover by rolling back.
                case WORKING :
                case PREPARING :
                {
                    for (DeployedFile file : deployment)
                    {
                        String path = deployment.getFileForPath(file.getPath()).getAbsolutePath();
                        File renamed = new File(path + ".alf");
                        File original = new File(path);
                        if (original.exists() && file.getType() != FileType.SETGUID)
                        {
                            Deleter.Delete(original);
                        }
                        if (renamed.exists())
                        {
                            renamed.renameTo(original);
                        }
                    }
                    deployment.rollback();
                    logger.info("recover action=rollback for ticket:" + ticket);
                    return false;
                }
                // In this case the only thing we can do is go forward because
                // we have all the new data in place and some of the original data may
                // already be gone.
                case COMMITTING :
                {
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
                    logger.info("recover action=commit for ticket:" + ticket);
                    return true;
                }
                default :
                {
                	logger.error("unknown state for recovery of ticket:" + ticket);
                    throw new DeploymentException("recover called while state = " + deployment.getState());
                }
            }
        }
        catch (Exception e)
        {
        	logger.error("Recovery failed for " + ticket, e);
            throw new DeploymentException("Recovery failed for ticket:" + ticket, e);
        }
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.deployment.DeploymentReceiverService#delete(java.lang.String, java.lang.String)
     */
    public synchronized void delete(String ticket, String path)
    {
        Deployment deployment = fDeployments.get(ticket);
        if (deployment == null)
        {
           	String msg = "Could not delete because invalid ticket:" + ticket;
            throw new DeploymentException(msg);
        }
        try
        {
        	File f = deployment.getFileForPath(path);
        	boolean exists = f.exists();
        	
            if (!exists) {
            	deployment.setMetaError(true);
        		logger.warn("unable to delete, does not exist, path:" + f.getAbsolutePath());
            	if(isErrorOnOverwrite())
            	{
            		throw new DeploymentException("unable to delete, does not exist, path:" + f.getAbsolutePath());
            	}
            }
            
            DeployedFile file = new DeployedFile(FileType.DELETED, 
                                                 null,
                                                 path,
                                                 null,
                                                 false);
            deployment.add(file);
        }
        catch (IOException e)
        {
        	logger.debug("Could not update log. Aborted.", e);
        	try {
        		abort(ticket);
        	} catch (Exception err) {
        		// exception thrown in abort
        		logger.error(err);
        	}
            throw new DeploymentException("Could not update log.", e);
        }
    }

    /* (non-Javadoc)
     * @see org.alfresco.deployment.DeploymentReceiverService#finishSend(java.lang.String, java.io.OutputStream)
     */
    public void finishSend(String token, OutputStream out)
    {
        Deployment deployment = fDeployments.get(token);
        if (deployment == null)
        {
            throw new DeploymentException("Deployment timed out or invalid ticket.");
        }
        try
        {
            DeployedFile file = deployment.getDeployedFile(out);
            deployment.closeOutputStream(out);
            fReaders.closeCopyThread(file);
            deployment.add(file);
        }
        catch (IOException e)
        {
            // TODO Do cleanup.
          	logger.error("finishSend",  e);
            throw new DeploymentException("FinishSend I/O error.", e);
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
            throw new DeploymentException("getListing invalid ticket. ticket:" + ticket);
        }
        try
        {
        	/**
        	 * get the listing
        	 * could have meta and a dir exists - good this is what we expect
        	 * could have meta data but no dir - external person has deleted the dir - what to do here ?
        	 * could have a dir but no meta data - exception will get thrown - create empty metadata ?
        	 */
        	File f = deployment.getFileForPath(path);
        	boolean exists = f.exists();
        	Set<FileDescriptor> list = deployment.getListing(path);
        	
        	if(!exists)
        	{
        		// here got some meta data, but directory is missing, parent metadata is corrupt
            	// create dir, return create empty meta data ?
            	throw new DeploymentException("Directory is missing, path:" + f.getAbsolutePath());
            }
        	
            return new ArrayList<FileDescriptor>(list);
            
        }
        catch (Exception e)
        {
        	// TODO do we create metadata here and try to recover ?
        	try 
        	{
        		abort(ticket);
        	} catch (Exception err) {
        		// exception thrown in abort
        		logger.error(err);
        	}
            throw new DeploymentException("Could not get listing for path:" + path, e);
        }
    }

    /* (non-Javadoc)
     * @see org.alfresco.deployment.DeploymentReceiverService#mkdir(java.lang.String, java.lang.String, java.lang.String)
     */
    public synchronized void mkdir(String ticket, String path, String guid)
    {
        Deployment deployment = fDeployments.get(ticket);
        if (deployment == null)
        {
            throw new DeploymentException("mkdir invalid ticket. ticket:" + ticket);
        }
        
    	File f = deployment.getFileForPath(path);
    	boolean exists = f.exists();
    	
        if (exists) 
        {
        	deployment.setMetaError(true);
    		logger.warn("writing to pre-existing directory, path:" + f.getAbsolutePath());
        	if(isErrorOnOverwrite())
        	{
        		throw new DeploymentException("directory already exists, path:" + f.getAbsolutePath());
        	}
        }
        // create a new directory
        DeployedFile file = new DeployedFile(FileType.DIR,
                                             null,
                                             path,
                                             guid,
                                             !exists);
        try
        {
            deployment.add(file);
        }
        catch (IOException e)
        {
        	try {
        		abort(ticket);
        	} catch (Exception err) {
        		// exception thrown in abort
        		logger.error(err);
        	}
            throw new DeploymentException("Could not log mkdir of " + path + " error: " + e.toString(), e);
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
            
            PipedOutputStream pos = new PipedOutputStream();
            PipedInputStream pis = new PipedInputStream();
            pis.connect(pos);
             
            // add input filters here
            InputStream is = pis;
            if(transformers != null && transformers.size() > 0) 
            {
            	for (DeploymentTransportInputFilter transformer : transformers)
            	{
            		is = transformer.addFilter(is, path);
            	}
            }

            // Open the destination file
            OutputStream out = new FileOutputStream(preLocation);
            
        	File f = deployment.getFileForPath(path);
        	boolean exists = f.exists();
        	       
            DeployedFile file = new DeployedFile(FileType.FILE,
                                                 preLocation,
                                                 path,
                                                 guid,
                                                 !exists);
            
            deployment.addOutputStream(pos, file);

            // Need to kick off a reader thread to process input 
            fReaders.addCopyThread(is, out, file);
            
            return pos;
        }
        catch (IOException e)
        {
        	try {
        		abort(ticket);
        	} catch (Exception err) {
        		// exception thrown in abort
        		logger.error(err);
        	}
            throw new DeploymentException("Unable to open " + path + " for write.", e);
        }
    }

    /* (non-Javadoc)
     * @see org.alfresco.deployment.DeploymentReceiverService#shutDown(java.lang.String, java.lang.String)
     */
    public synchronized void shutDown(String user, String password)
    {
    	// TODO - how to check user password given that we may have multiple targets each with their own details
    	shutDown();
    	
    	fContext.close();
        //System.exit(0);
    }

    /** 
     * This is the keep-alive thread of the FSR.
     * When fDone = true this thread exits and the JVM will terminate.
     * 
     * And since we have to have a thread - may as well use it to process our event queues.
     * 
     * @see java.lang.Runnable#run()
     */
    public void run()
    {
    	logger.info("Alfresco File System Receiver Started");
        while (!fDone)
        {     
            // process validation queue
            Target toValidate = validateQueue.poll();
            if(toValidate != null)
            {
            	try 
            	{
            		boolean lockHeld = false;
            		synchronized(toValidate){
            			if(toValidate.isBusy())
            			{
            				// do no validation there is a deployment in progress
            				logger.warn("target is busy. Not validating target:" + toValidate.getName());
            			}
            			else
            			{
            				toValidate.setBusy(true);
            				lockHeld = true;
            			}
            		}
            		if(lockHeld)
            		{
            			try 
            			{
            				logger.info("Validation starting for target:" + toValidate.getName() );
            				toValidate.validateMetaData();
            				logger.info("Validation finished");
            			} 
            			finally 
            			{
            				toValidate.setBusy(false);
            			}
            		}
            	}
            	catch (Exception e)
            	{
            		logger.error("Unable to validate", e);
            	}
            }
            
            try
            {
                Thread.sleep(5000);
            }
            catch (InterruptedException e)
            {
                // Finished Sleeping - fDone may have been set if we are shutting down.
            }
    
        }
        logger.info("Alfresco File System Receiver Stopped");
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        fContext = (ConfigurableApplicationContext)applicationContext;
    }

    public void setGuid(String ticket, String path, String guid)
    {
        Deployment deployment = fDeployments.get(ticket);
        if (deployment == null)
        {
            throw new DeploymentException("Deployment invalid ticket.");
        }
        try
        {
            deployment.setGuid(path, guid);
        }
        catch (Exception e)
        {
        	try {
        		abort(ticket);
        	} catch (Exception err) {
        		// exception thrown in abort
        		logger.error(err);
        	}
            throw new DeploymentException("Could not set guid on " + path, e);
        }
    }
    
    /**
     * The transformers to apply to the incoming messages
     */
	List<DeploymentTransportInputFilter> transformers;
	 
	/**
	 * Get the content transformers for this transport - if the transport does not support
	 * content transformation then simply return null;
	 * @return the content transformers or null if there are no transformers.
	 */
	public List<DeploymentTransportInputFilter>getTransformers() {
		return transformers;
	}
	
	public void setTransformers( List<DeploymentTransportInputFilter> transformers) {
	    this.transformers = transformers;
	}

	/**
	 * Should there be an error if the FSR attempts to create a file or directory 
	 * that already exists ?   Otherwise the FSR will issue a warning and carry on.
	 * 
	 * @param errorOnOverwrite true an error will occur and deployment will stop, false 
	 * a warning will occur and deployment will continue
	 */
	public void setErrorOnOverwrite(boolean errorOnOverwrite) {
		this.errorOnOverwrite = errorOnOverwrite;
	}

	public boolean isErrorOnOverwrite() {
		return errorOnOverwrite;
	}
	
	/**
	 * Part of the commit process.
	 */
	private class CommitWriterThread extends CommitThread {
		
		private LinkedBlockingQueue<DeployedFile> queue;
		private Deployment deployment; 
		
		CommitWriterThread(Deployment deployment, LinkedBlockingQueue<DeployedFile> queue) 
		{
			this.deployment = deployment;
			this.queue = queue;
		}
		
		/**
		 * 
		 */
		public void run() 
		{           
			while(getException() == null) 
			{
	            DeployedFile file = null;
				try {
					file = queue.poll(3, TimeUnit.SECONDS);
				} 
				catch (InterruptedException e1) 
				{
					logger.debug("interrupted");
				}
				
	            if(file == null) 
	            {
	            	if(isFinish()) 
	            	{
	            			logger.debug("committer thread finished normally");
	            			break;
	            	}
	        	}
	            else
	            {
	            	try 
	            	{
	            		String path = file.getPath();
	            		switch (file.getType())
	            		{
	                        case FILE :
	                        {
	                        	logger.debug("add file:" + path);
	                        	// If file already exists then rename it
	                        	File f = deployment.getFileForPath(path);
	                        	if (f.exists())
	                        	{
	                        		File dest = new File(f.getAbsolutePath() + ".alf");
	                        		f.renameTo(dest);
	                        		f = deployment.getFileForPath(path);
	                        	}
	                        	// copy the file from the preLocation to its final target location
	                           	FileOutputStream out = new FileOutputStream(f);
	                        	FileInputStream in = new FileInputStream(file.getPreLocation());
	                       
	                        	FileChannel outChannel = out.getChannel(); 
	                        	FileChannel inChannel = in.getChannel();
	                        		
	                        	// Chunk size is required to use NIO on large files
	                            int chunkSize = 1 * (1024 * 1024);
	                            long size = inChannel.size();
	                            long position = 0;
	                            while (position < size) {
	                               position += inChannel.transferTo(position, chunkSize, outChannel);
	                            }
	                        	in.close();
	                        	out.flush();
	                        	out.close();
	                        		
	                        	break;
	                        }
	                        case DELETED :
	                        {
	                        	logger.debug("delete file:" + path);
	                        	// prepare the file for deletion by renaming it
	                        	File f = deployment.getFileForPath(path);
	                        	if (f.exists())
	                        	{
	                        		File dest = new File(f.getAbsolutePath() + ".alf");
	                        		f.renameTo(dest);
	                        	}
	                        	break;
	                        }
	                    }
	            	} 
	            	catch (Exception e) 
	            	{
	            		logger.debug("exception in committer thread", e);
	            		setException(e);
	            	}
	            }
			}
		}		
	}
	
	/**
	 * Part of the commit process.
	 */
	private class CommitMetaClonerThread extends CommitThread {
		
		private Deployment deployment; 
		
		CommitMetaClonerThread(Deployment deployment) 
		{
			this.deployment = deployment;
		}
		
		/**
		 * run the metadata cloner
		 */
		public void run() 
		{
			try 
			{
	        	// Clone metadata etc
	            deployment.prepare();
	            logger.debug("metadata cloned and prepared");
			} 
		    catch (Exception e)
			{
		    	setException(e);
			}
		}
	}
	/**
	 * Used by the commit method
	 */
	private abstract class CommitThread extends Thread {
		
		private Exception exception;
		
		private boolean stop = false;
		
		public boolean isFinish()
		{
			return stop;
		}
		
		/** 
		 * Called to stop this thread
		 */
		public void setFinish() 
		{
			stop = true;
		}
		
		public Exception getException()
		{
			return exception;
		}
		
		public void setException(Exception e)
		{
			this.exception = e;
		}
	}
}
