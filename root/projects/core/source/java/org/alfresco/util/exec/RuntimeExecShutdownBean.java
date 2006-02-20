/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the Mozilla Public License version 1.1 
 * with a permitted attribution clause. You may obtain a
 * copy of the License at
 *
 *   http://www.alfresco.org/legal/license.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.util.exec;

import java.util.Collections;
import java.util.List;

import org.alfresco.util.exec.RuntimeExec.ExecutionResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * This bean executes a list of shutdown commands when either the VM shuts down
 * or the application context closes.  In both cases, the commands are only
 * executed if the application context was started.
 * 
 * @author Derek Hulley
 */
public class RuntimeExecShutdownBean implements ApplicationListener
{
    private static Log logger = LogFactory.getLog(RuntimeExecShutdownBean.class);
    
    /** the commands to execute on context closure or VM shutdown */
    private List<RuntimeExec> shutdownCommands;
    /** the registered shutdown hook */
    private Thread shutdownHook;
    /** ensures that commands don't get executed twice */
    private boolean executed;

    /**
     * Initializes the bean with empty defaults, i.e. it will do nothing
     */
    public RuntimeExecShutdownBean()
    {
        this.shutdownCommands = Collections.emptyList();
        this.executed = false;
    }

    /**
     * Set the commands to execute, in sequence, when the application context
     * is initialized.
     * 
     * @param startupCommands list of commands
     */
    public void setShutdownCommands(List<RuntimeExec> startupCommands)
    {
        this.shutdownCommands = startupCommands;
    }

    /**
     * Listens for the the context refresh and executes the startup commands.
     * Any failure of the commands will lead to context initialization failure.
     */
    public synchronized void onApplicationEvent(ApplicationEvent event)
    {
        if (event instanceof ContextRefreshedEvent)
        {
            // register shutdown hook
            shutdownHook = new ShutdownThread();
            Runtime.getRuntime().addShutdownHook(shutdownHook);

            if (logger.isDebugEnabled())
            {
                logger.debug("Registered shutdown hook");
            }
        }
        else if (event instanceof ContextClosedEvent)
        {
            // remove shutdown hook and execute
            if (shutdownHook != null)
            {
                // execute
                execute();
                // remove hook
                try
                {
                    Runtime.getRuntime().removeShutdownHook(shutdownHook);
                }
                catch (IllegalStateException e)
                {
                    // VM is already shutting down
                }
                shutdownHook = null;
                
                if (logger.isDebugEnabled())
                {
                    logger.debug("Deregistered shutdown hook");
                }
            }
        }
    }
    
    private synchronized void execute()
    {
        // have we already done this?
        if (executed)
        {
            return;
        }
        executed = true;
        for (RuntimeExec command : shutdownCommands)
        {
            ExecutionResult result = command.execute();
            // check for failure
            if (!result.getSuccess())
            {
                logger.error("Shutdown command execution failed.  Continuing with other commands.: \n" + result);
            }
        }
        // done
        if (logger.isDebugEnabled())
        {
            logger.debug("Executed shutdown commands");
        }
    }
    
    /**
     * The thread that will call the shutdown commands.
     * 
     * @author Derek Hulley
     */
    private class ShutdownThread extends Thread
    {
        private ShutdownThread()
        {
            this.setDaemon(true);
        }

        @Override
        public void run()
        {
            execute();
        }
    }
}














