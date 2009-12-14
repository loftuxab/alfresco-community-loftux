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

package org.alfresco.deployment.impl.fsr;

import java.io.File;


import org.alfresco.deployment.impl.server.DeploymentCommandQueue;
import org.springframework.extensions.surf.util.PropertyCheck;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FileSystemReceiverServiceImpl implements FileSystemReceiverService
{
    private boolean errorOnOverwrite = false;
    


	private String fLogDirectory;

	private String fDataDirectory;
	
    private DeploymentCommandQueue commandQueue;

	private static Log logger = LogFactory.getLog(FileSystemReceiverServiceImpl.class);



	public void setLogDirectory(String logDirectory)
	{
		fLogDirectory = logDirectory;
	}

	public void setDataDirectory(String dataDirectory)
	{
		fDataDirectory = dataDirectory;
	}

	@SuppressWarnings("unchecked")
	public void init()
	{

		PropertyCheck.mandatory(this, "dataDirectory", fDataDirectory);
		PropertyCheck.mandatory(this, "logDirectory", fLogDirectory);
		PropertyCheck.mandatory(this, "commandQueue", commandQueue);


		File log = new File(fLogDirectory);
		if (!log.exists())
		{
			logger.info("creating log data directory:" + log.toString());
			log.mkdirs();
		}
		File data = new File(fDataDirectory);
		if (!data.exists())
		{
			logger.info("creating data directory:" + data.toString());
			data.mkdirs();
		}
	}

	/**
	 * Get the directory to which log (as in journal) files will be written.
	 * @return
	 */
	public String getLogDirectory()
	{
		return fLogDirectory;
	}

	/**
	 * Get the directory to which work phase files get written.
	 * @return
	 */
	public String getDataDirectory()
	{
		return fDataDirectory;
	}
	
	/**
	 * Should there be an error if the FSR attempts to create a file or directory 
	 * that already exists ?   Otherwise the FSR will issue a warning and carry on.
	 * 
	 * @param errorOnOverwrite true an error will occur and deployment will stop, false 
	 * a warning will occur and deployment will continue
	 */
	public void setErrorOnOverwrite(boolean errorOnOverwrite) 
	{
		this.errorOnOverwrite = errorOnOverwrite;
	}

	public boolean isErrorOnOverwrite() 
	{
		return errorOnOverwrite;
	}

	public void queueCommand(Runnable command) 
	{
		commandQueue.queueCommand(command);
	}

	public Runnable pollCommand()
	{
		return commandQueue.pollCommand();
	}
	
	public void setCommandQueue(DeploymentCommandQueue commandQueue) 
	{
		this.commandQueue = commandQueue;
	}

	public DeploymentCommandQueue getCommandQueue() 
	{
		return commandQueue;
	}



}
