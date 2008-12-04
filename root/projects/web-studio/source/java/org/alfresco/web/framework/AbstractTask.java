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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Abstract implementation of a task which may be useful to developers who
 * wish to write custom tasks.
 * 
 * @author muzquiano
 */
public abstract class AbstractTask implements Task
{
    protected static Log logger = LogFactory.getLog(AbstractTask.class);
    
    protected boolean isError = false;
    protected boolean isSuccess = false;
    protected boolean isFinished = false;
    protected boolean isRunning = false;
    protected boolean isCancelled = false;
    protected Throwable throwable = null;
    protected String id = null;
	protected String name = null;
	protected String description = null;
	protected String status;
	protected int progress;
	protected int progressSize;
	protected List<String> history;
	protected String creator;
	protected Date startTime;
	protected Date endTime;

	/**
	 * Instantiates a new abstract task.
	 * 
	 * @param name the name
	 */
	public AbstractTask(String name)
	{
		this.name = name;
		this.id = new org.alfresco.tools.ObjectGUID().toString();

		this.history = new ArrayList<String>();
		this.description = "";
		this.status = "";
		this.creator = "unknown";
		this.startTime = new Date();

		progress = 0;
		progressSize = 10;
	}

	/* (non-Javadoc)
	 * @see org.alfresco.web.framework.Task#increment()
	 */
	public void increment()
	{
		progress++;
	}

	/* (non-Javadoc)
	 * @see org.alfresco.web.framework.Task#getId()
	 */
	public String getId()
	{
		return id;
	}

	/* (non-Javadoc)
	 * @see org.alfresco.web.framework.Task#getName()
	 */
	public String getName()
	{
		return name;
	}

	/* (non-Javadoc)
	 * @see org.alfresco.web.framework.Task#setDescription(java.lang.String)
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}

	/* (non-Javadoc)
	 * @see org.alfresco.web.framework.Task#getDescription()
	 */
	public String getDescription()
	{
		return description;
	}

	/* (non-Javadoc)
	 * @see org.alfresco.web.framework.Task#setCreator(java.lang.String)
	 */
	public void setCreator(String creator)
	{
		this.creator = creator;
	}

	/* (non-Javadoc)
	 * @see org.alfresco.web.framework.Task#getCreator()
	 */
	public String getCreator()
	{
		return this.creator;
	}

	/* (non-Javadoc)
	 * @see org.alfresco.web.framework.Task#execute()
	 */
	public abstract void execute()
		throws Throwable;

	/* (non-Javadoc)
	 * @see org.alfresco.web.framework.Task#cancel()
	 */
	public abstract void cancel();

	/* (non-Javadoc)
	 * @see org.alfresco.web.framework.Task#isError()
	 */
	public boolean isError()
	{
		return isError;
	}

	/* (non-Javadoc)
	 * @see org.alfresco.web.framework.Task#isSuccess()
	 */
	public boolean isSuccess()
	{
		return isSuccess;
	}

	/* (non-Javadoc)
	 * @see org.alfresco.web.framework.Task#isFinished()
	 */
	public boolean isFinished()
	{
		return isFinished;
	}

	/* (non-Javadoc)
	 * @see org.alfresco.web.framework.Task#isRunning()
	 */
	public boolean isRunning()
	{
		return isRunning;
	}

	/* (non-Javadoc)
	 * @see org.alfresco.web.framework.Task#isCancelled()
	 */
	public boolean isCancelled()
	{
		return isCancelled;
	}

	/* (non-Javadoc)
	 * @see org.alfresco.web.framework.Task#getStartTime()
	 */
	public Date getStartTime()
	{
		return this.startTime;
	}

	/* (non-Javadoc)
	 * @see org.alfresco.web.framework.Task#getEndTime()
	 */
	public Date getEndTime()
    {
        return this.endTime;
    }

	/* (non-Javadoc)
	 * @see org.alfresco.web.framework.Task#getThrowable()
	 */
	public Throwable getThrowable()
	{
		return throwable;
	}

	/* (non-Javadoc)
	 * @see org.alfresco.web.framework.Task#getStatus()
	 */
	public String getStatus()
	{
		return this.status;
	}

	/* (non-Javadoc)
	 * @see org.alfresco.web.framework.Task#setStatus(java.lang.String)
	 */
	public void setStatus(String status)
	{
		this.status = status;

		String pattern = "yyyy, MMMMM, d hh:mm";
		java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat(pattern);
		String result = formatter.format(new Date());
		String text = "[" + result + "] " + status;

		history.add(text);
	}

	/* (non-Javadoc)
	 * @see org.alfresco.web.framework.Task#getProgress()
	 */
	public int getProgress()
	{
		return this.progress;
	}

	/* (non-Javadoc)
	 * @see org.alfresco.web.framework.Task#getProgressSize()
	 */
	public int getProgressSize()
	{
		return this.progressSize;
	}

	/* (non-Javadoc)
	 * @see org.alfresco.web.framework.Task#getHistory()
	 */
	public List<String> getHistory()
	{
		return history;
	}
}