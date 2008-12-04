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
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.web.framework;

import java.util.Date;
import java.util.List;

/**
 * Task interface
 * 
 * @author muzquiano
 */
public interface Task
{
    /**
     * Returns the internal id of the task
     * 
     * @return id
     */
    public String getId();

    /**
     * Returns the name of the task
     * 
     * @return name
     */
    public String getName();

    /**
     * Sets a description for the task
     * 
     * @param description
     */
    public void setDescription(String description);

    /**
     * Returns the description of the task
     * 
     * @return description
     */
    public String getDescription();

    /**
     * Sets the creator of the task
     * 
     * @param creator
     */
    public void setCreator(String creator);

    /**
     * Returns the creator of the task
     * 
     * @return creator
     */
    public String getCreator();

    /**
     * Returns how far the task has progressed down the progress bar.
     * 
     * @return progress count
     */
    public int getProgress();

    /**
     * Returns the total size of the progress bar.
     * 
     * @return progress bar total size
     */
    public int getProgressSize();

    /**
     * Increments the progress
     */
	public void increment();

	/**
	 * Executes the task
	 * 
	 * @throws Throwable
	 */
	public void execute() throws Throwable;

	/**
	 * Cancels the task
	 */
	public void cancel();

	/**
	 * Whether the task resulted in an error
	 * 
	 * @return
	 */
	public boolean isError();

	/**
	 * Whether the task completed successfully
	 * 
	 * @return
	 */
	public boolean isSuccess();

	/**
	 * Whether the tasks completed
	 * 
	 * @return
	 */
	public boolean isFinished();

	/**
	 * Whether the task is still running
	 * 
	 * @return
	 */
	public boolean isRunning();

	/**
	 * Whether the task was cancelled
	 * 
	 * @return
	 */
	public boolean isCancelled();

	/**
	 * The start time of the task
	 * 
	 * @return
	 */
	public Date getStartTime();
	
	/**
	 * The end time of the task
	 * 
	 * @return
	 */
	public Date getEndTime();

	/**
	 * If the task resulted in an error, retrieves the throwable obtained by
	 * the task worker thread.
	 * 
	 * @return
	 */
	public Throwable getThrowable();

	/**
	 * Sets the status
	 * 
	 * @param status
	 */
	public void setStatus(String status);
	
	/**
	 * Current status of the task
	 * 
	 * @return
	 */
	public String getStatus();

	/**
	 * Full history of all status updates to the task
	 * 
	 * @return
	 */
	public List<String> getHistory();
}