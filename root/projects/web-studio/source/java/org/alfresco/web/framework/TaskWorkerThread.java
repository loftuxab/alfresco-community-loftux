/*
 * Copyright (C) 2005-2008 Alfresco Software Limited.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 * 
 * As a special exception to the terms and conditions of version 2.0 of the GPL,
 * you may redistribute this Program in connection with Free/Libre and Open
 * Source Software ("FLOSS") applications as described in Alfresco's FLOSS
 * exception. You should have recieved a copy of the text describing the FLOSS
 * exception, and it is also available here:
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.web.framework;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Worker thread implementation that is responsible for executing a task,
 * updating task state and then falling back asleep until called upon once more.
 * 
 * @author muzquiano
 */
public class TaskWorkerThread extends Thread
{
    private static Log logger = LogFactory.getLog(TaskWorkerThread.class);

    private AbstractTask task = null;
    private TaskManager taskManager = null;

    public TaskWorkerThread(String name, TaskManager taskManager)
    {
        super(name);
        super.setDaemon(true);
        this.taskManager = taskManager;
    }

    public AbstractTask getTask()
    {
        return this.task;
    }

    public void setTask(AbstractTask task)
    {
        this.task = task;
    }

    public Object getMutex()
    {
        return taskManager.getClass();
    }

    public void run()
    {
        while (true)
        {
            try
            {
                synchronized (getMutex())
                {
                    // wait until we are told that there is some work to do
                    getMutex().wait();

                    // let's make sure there is a task available to do
                    AbstractTask task = (AbstractTask) taskManager.getTaskQueue().peek();
                    if (task != null)
                    {
                        task = (AbstractTask) taskManager.getTaskQueue().poll();
                        
                        // some debug info
                        if (logger.isDebugEnabled())
                        {
                            logger.debug("Found a job to start (" + task.getName() + ")");
                        }

                        // set this as the active task
                        setTask(task);
                    }
                }
            }
            catch (InterruptedException ie)
            {
            }

            // if we have a task, let's run it
            if (getTask() != null)
            {
                // reset the task
                getTask().progress = 0;
                getTask().setStatus("Starting...");

                // mark that the job is running
                getTask().isRunning = true;
                getTask().isFinished = false;
                getTask().isError = false;
                getTask().isSuccess = false;

                if (logger.isDebugEnabled())
                {
                    logger.debug("Running job " + getTask().getId());
                }

                try
                {
                    getTask().execute();
                    getTask().isSuccess = true;
                }
                catch (Throwable t)
                {
                    getTask().throwable = t;
                    t.printStackTrace();
                    getTask().isError = true;
                }

                if (getTask().isCancelled() && logger.isDebugEnabled())
                {
                    getTask().isSuccess = false;
                    
                    logger.debug("Finished with a cancelled job");
                }

                // mark that the job has completed
                if (logger.isDebugEnabled())
                {
                    logger.debug("Completing job " + getTask().getId());
                }

                getTask().setStatus("Finished");
                getTask().isFinished = true;
                getTask().isRunning = false;

                // now unbind the job from this thread, we're done working
                setTask(null);
            }
        }
    }
}
