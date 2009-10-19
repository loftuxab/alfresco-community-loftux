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

import java.util.HashMap;
import java.util.Queue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Basic implementation of a task manager for use in asynchronous 
 * processing and monitoring of tasks.
 * 
 * Tasks are added to the task manager.  When added, they are 
 * assigned a reference id so that subsequent calls to the task
 * manager can check on the task status.
 * 
 * A task starter thread wakes up occasionally and checks whether 
 * there are any tasks waiting to be processed.
 * 
 * If a task is found, it is attached to a task worker thread and 
 * then executed.  When the task worker thread completes, it is 
 * handed back to the task worker thread pool.
 * 
 * @author muzquiano
 */
public class TaskManager
{
    private static Log logger = LogFactory.getLog(TaskManager.class);

    // the thread launcher thread
    private TaskStarterThread taskStarterThread = null;
    
    // our collection of worker threads
    private HashMap<String, TaskWorkerThread> threads;

    // tasks that are queued up for us to do
    private Queue<AbstractTask> taskQueue = null;

    // all tasks that are running
    private HashMap<String, AbstractTask> allTasks = null;

    // maximum number of tasks we should run concurrently
    private int threadPoolSize = 5;
    
    // how often the nightcrawler should wake up to check
    // if there are new jobs to process
    private int wakeupPeriod = 1000;

    public TaskManager()
    {
    }
    
    public void init()
    {
        // initialize all collections
        taskQueue = new java.util.PriorityQueue<AbstractTask>();
        allTasks = new HashMap<String, AbstractTask>();
        threads = new HashMap<String, TaskWorkerThread>();        

        // create the nightkeeper thread that will make sure
        // that tasks are being processed
        taskStarterThread = new TaskStarterThread("TaskManager-Starter", this);
        taskStarterThread.start();

        // stock our available threads
        for (int i = 0; i < getThreadPoolSize(); i++)
        {
            TaskWorkerThread thread = new TaskWorkerThread("TaskManager-"+ i, this);
            getThreads().put(thread.getName(), thread);
            thread.start();

            if (logger.isDebugEnabled())
                logger.debug("Added WorkerThread to pool (" + (i + 1) + " of " + getThreadPoolSize() + ")");
        }        
    }
    
    public void setThreadPoolSize(int threadPoolSize)
    {
        this.threadPoolSize = threadPoolSize;
    }
    
    public int getThreadPoolSize()
    {
        return this.threadPoolSize;
    }
    
    public void setWakeupPeriod(int wakeupPeriod)
    {
        this.wakeupPeriod = wakeupPeriod;
    }
    
    public int getWakeupPeriod()
    {
        return this.wakeupPeriod;
    }

    public int getThreadCount()
    {
        return threads.size();
    }

    public int getTaskQueueCount()
    {
        return taskQueue.size();
    }

    public synchronized String addTask(AbstractTask task)
    {
        taskQueue.add(task);
        allTasks.put(task.getId(), task);

        // return the task id
        return task.getId();
    }

    /**
     * Returns a task that is running or has finished running
     */
    public AbstractTask getTask(String taskId)
    {
        return (AbstractTask) allTasks.get(taskId);
    }

    /**
     * Returns all of the tasks
     */
    public HashMap<String, AbstractTask> getAllTasks()
    {
        return allTasks;
    }

    public void remove(String taskId)
    {
        AbstractTask task = getTask(taskId);
        if (task != null)
        {
            remove(task);
        }
    }

    public void remove(AbstractTask task)
    {
        if (task != null && task.isFinished())
        {
            allTasks.remove(task.getId());
        }
    }

    public Queue<AbstractTask> getTaskQueue()
    {
        return taskQueue;
    }

    public HashMap<String, TaskWorkerThread> getThreads()
    {
        return threads;
    }

    public void cancel(String taskId)
    {
        AbstractTask task = getTask(taskId);
        if (task != null)
        {
            task.setStatus("Cancel called for the task");
            task.cancel();
            task.setStatus("Cancellation completed");
            task.isCancelled = true;
        }
    }
}
