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

import java.util.Queue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Background thread that wakes up periodically to monitor the task manager's
 * pool of tasks waiting to be processed. If it finds one, it notifies all
 * worker threads so that someone will pick up the task and begin to work on it.
 * 
 * @author muzquiano
 */
public class TaskStarterThread extends Thread
{
    private static Log logger = LogFactory.getLog(TaskStarterThread.class);

    protected TaskManager taskManager;

    /**
     * Instantiates a new task starter thread.
     */
    public TaskStarterThread(String name, TaskManager taskManager)
    {
        super(name);
        this.taskManager = taskManager;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Thread#run()
     */
    public void run()
    {
        while (true)
        {
            try
            {
                // sleep for a bit
                Thread.sleep(taskManager.getWakeupPeriod());
                
                // check whether the task queue has anything in it
                Queue<AbstractTask> taskQueue = taskManager.getTaskQueue();
                
                // if there are tasks to do
                if (taskQueue.size() > 0)
                {
                    // peek at the first tasks in the queue
                    AbstractTask task = (AbstractTask) taskQueue.peek();
                    if(task != null)
                    {
                        // in fact, there is a task...
                        if (logger.isDebugEnabled())
                        {
                            logger.debug("Found a job to start (" + task.getName() + ")");
                            logger.debug("Attempting to wake up threads");
                        }
    
                        // wake up all of the workers who are waiting
                        synchronized (taskManager.getClass())
                        {
                            taskManager.getClass().notifyAll();
                        }
                    }
                }
            }
            catch (Throwable t)
            {
                t.printStackTrace();
            }
        }
    }

}
